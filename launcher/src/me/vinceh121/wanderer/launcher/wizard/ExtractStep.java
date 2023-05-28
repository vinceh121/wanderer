package me.vinceh121.wanderer.launcher.wizard;

import java.awt.BorderLayout;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import me.vinceh121.n2ae.model.NvxFileReader;
import me.vinceh121.n2ae.pkg.NnpkFileExtractor;
import me.vinceh121.n2ae.pkg.NnpkFileReader;
import me.vinceh121.n2ae.pkg.TableOfContents;
import me.vinceh121.n2ae.script.NOBClazz;
import me.vinceh121.n2ae.script.ParseException;
import me.vinceh121.n2ae.texture.NtxFileReader;
import me.vinceh121.wanderer.launcher.LauncherMain;

public class ExtractStep extends AbstractWizardStep {
	private static final long serialVersionUID = 1L;
	private final JScrollPane scroll = new JScrollPane();
	private final JTextArea textArea = new JTextArea();
	private final JProgressBar progressBar = new JProgressBar();
	private final ExtractWorker worker = new ExtractWorker();

	public ExtractStep(final FirstTimeWizardContext ctx) {
		super(ctx);
		this.setLayout(new BorderLayout(15, 15));

		this.textArea.setEditable(false);
		this.scroll.setViewportView(this.textArea);
		this.add(this.scroll, BorderLayout.CENTER);

		this.add(this.progressBar, BorderLayout.SOUTH);

		this.worker.addPropertyChangeListener(e -> {
			if ("progress".equals(e.getPropertyName())) {
				this.progressBar.setValue(this.worker.getProgress());
			}
		});
	}

	@Override
	public void onSwitchTo() {
		this.worker.execute();
	}

	@Override
	public boolean canNextStep() {
		if (!this.worker.isDone()) {
			JOptionPane.showMessageDialog(null, "Extraction is in progress", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	private class ExtractWorker extends SwingWorker<Void, String> {
		private Map<String, NOBClazz> model; // scripts need to be read cause that's where the skinning bones are
												// defined
		private int fileCount = 0, filesProcessed;

		private void countFilesRecurse(final TableOfContents toc) {
			if (toc.isDirectory()) {
				for (final TableOfContents child : toc.getEntries().values()) {
					this.countFilesRecurse(child);
				}
			} else if (toc.isFile() && (toc.getName().endsWith(".ntx") || toc.getName().endsWith(".nvx"))) {
				this.fileCount++;
			}
		}

		@Override
		protected Void doInBackground() throws Exception {
			try {
				this.doInBackground0();
			} catch (final IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error while extracting: " + e);
			}
			return null;
		}

		private void doInBackground0() throws IOException {
			final File output = LauncherMain.getAssetsPath().toFile();
			output.mkdir();
			this.publish("Extracting NPK...");
			try (InputStream npkIn = Files.newInputStream(ExtractStep.this.ctx.getDataPath())) {
				final NnpkFileReader r = new NnpkFileReader(npkIn);
				r.readAll();

				this.countFilesRecurse(r.getTableOfContents());

				final NnpkFileExtractor extract = new NnpkFileExtractor(npkIn);
				extract.setOutput(output);
				extract.extractAllFiles(r.getTableOfContents());
			}
			this.publish("Extracted NPK.");
			this.publish("Converting assets...");
			this.recurse(output);
		}

		private void recurse(final File file) {
			if (file.isDirectory()) {
				for (final File f : file.listFiles()) {
					this.recurse(f);
				}
			} else {
				try {
					this.processFile(file);
				} catch (final Exception e) {
					System.err.println("Failed to process " + file.getPath());
					e.printStackTrace();
				}
			}
		}

		private void processFile(final File file) throws IOException, ParseException {
			if (!file.getName().contains(".")) {
				this.unprocessableFile(file);
			}
			final String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);

			final String outPath = file.toPath()
				.resolveSibling(file.getName().substring(0, file.getName().length() - extension.length() - 1))
				.toString();

			switch (extension) {
			case "nvx":
				this.publish("Converting model " + file);
				this.processModel(file, new File(outPath + ".obj"));
				this.incProcessedCount();
				file.delete();
				break;
			case "ntx":
				this.publish("Converting texture " + file);
				this.processTexture(file, new File(outPath + ".ktx"));
				this.incProcessedCount();
				file.delete();
				break;
			}
		}

		private void processTexture(final File fileIn, final File fileOut) throws IOException {
			try (FileInputStream is = new FileInputStream(fileIn);
					FileOutputStream os = new FileOutputStream(fileOut)) {
				final NtxFileReader r = new NtxFileReader(is);
				r.readHeader();
				r.readAllRaws();

				r.writeKtx(new DataOutputStream(os));
			}
		}

		private void processModel(final File fileIn, final File fileOut) throws IOException {
			try (FileInputStream is = new FileInputStream(fileIn);
					FileOutputStream os = new FileOutputStream(fileOut)) {
				final NvxFileReader r = new NvxFileReader(is);
				r.readAll();
				r.writeObj(os);
			}
		}

		private void incProcessedCount() {
			this.filesProcessed++;
			this.setProgress(100 * this.filesProcessed / this.fileCount);
		}

		private void unprocessableFile(final File file) {
			this.publish("Don't know how to process file " + file.getPath());
			file.delete();
		}

		@Override
		protected void process(final List<String> chunks) {
			for (final String s : chunks) {
				ExtractStep.this.textArea.append(s);
				ExtractStep.this.textArea.append("\n");
			}
			ExtractStep.this.scroll.getVerticalScrollBar().setValue(ExtractStep.this.scroll.getVerticalScrollBar().getMaximum());
		}
	}
}
