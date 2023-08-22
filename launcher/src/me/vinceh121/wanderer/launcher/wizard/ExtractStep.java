package me.vinceh121.wanderer.launcher.wizard;

import java.awt.BorderLayout;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.vinceh121.n2ae.animation.Curve;
import me.vinceh121.n2ae.animation.NaxFileReader;
import me.vinceh121.n2ae.gltf.GLTFGenerator;
import me.vinceh121.n2ae.model.NvxFileReader;
import me.vinceh121.n2ae.pkg.NnpkFileExtractor;
import me.vinceh121.n2ae.pkg.NnpkFileReader;
import me.vinceh121.n2ae.pkg.TableOfContents;
import me.vinceh121.n2ae.script.IParser;
import me.vinceh121.n2ae.script.NOBClazz;
import me.vinceh121.n2ae.script.ParseException;
import me.vinceh121.n2ae.script.nob.NOBParser;
import me.vinceh121.n2ae.texture.NtxFileReader;
import me.vinceh121.wanderer.launcher.LauncherMain;
import me.vinceh121.wanderer.launcher.data.AnimationSources;
import me.vinceh121.wanderer.launcher.data.ExternalDownload;

public class ExtractStep extends AbstractWizardStep {
	private static final long serialVersionUID = 1L;
	private static final ObjectMapper MAPPER = new ObjectMapper();
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

		this.ctx.setNextEnabled(false);
		this.ctx.setPreviousEnabled(false);
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
		private int filesToProcessCount, filesProcessed;

		private void countFilesRecurse(final TableOfContents toc) {
			if (toc.isDirectory()) {
				for (final TableOfContents child : toc.getEntries().values()) {
					this.countFilesRecurse(child);
				}
			} else if (toc.isFile() && (toc.getName().endsWith(".ntx") || toc.getName().endsWith(".nvx"))) {
				this.filesToProcessCount++;
			}
		}

		@Override
		protected Void doInBackground() throws Exception {
			try {
				this.doInBackground0();
			} catch (final Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error while extracting: " + e);
			}
			return null;
		}

		private void doInBackground0() throws IOException, ParseException {
			final Path dataPath = ExtractStep.this.ctx.getDataPath();
			final Path origPath = LauncherMain.getAssetsPath().resolve("orig");
			final File outputOrig = origPath.toFile();
			outputOrig.mkdirs();

			this.publish("Reading metadata...");
			final List<AnimationSources> anims = MAPPER.readValue(
					getClass().getClassLoader().getResourceAsStream("me/vinceh121/wanderer/launcher/animations.json"),
					new TypeReference<List<AnimationSources>>() {
					});
			this.filesToProcessCount += anims.size();
			final List<ExternalDownload> downs = MAPPER.readValue(
					getClass().getClassLoader()
						.getResourceAsStream("me/vinceh121/wanderer/launcher/externalDownloads.json"),
					new TypeReference<List<ExternalDownload>>() {
					});
			this.filesToProcessCount += downs.size();

			this.model = MAPPER.readValue(
					getClass().getClassLoader()
						.getResourceAsStream("me/vinceh121/wanderer/launcher/project-nomads.classmodel.cdbed99c.json"),
					new TypeReference<Map<String, NOBClazz>>() {
					});

			this.publish("Copying feedback, voice lines, and music...");
			Files.walkFileTree(dataPath.getParent().resolve("book"), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path src, BasicFileAttributes attrs) throws IOException {
					if (!src.getFileName().toString().endsWith(".wav")) {
						return FileVisitResult.CONTINUE;
					}

					Path relPath = dataPath.getParent().resolve("book").relativize(src);
					publish("Copying sound " + relPath);
					Path dest = origPath.resolve("book").resolve(ctx.getVoice().getLocale()).resolve(relPath);
					Files.createDirectories(dest.getParent());
					Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
					return FileVisitResult.CONTINUE;
				}
			});

			// is this really necessary?
			try {
				Files.move(origPath.resolve("book").resolve(ctx.getVoice().getLocale()).resolve("feedback"),
						origPath.resolve("feedback"));
			} catch (FileAlreadyExistsException e) {
				publish("Refusing to move already existing feedback");
			}
			try {
				Files.move(origPath.resolve("book").resolve(ctx.getVoice().getLocale()).resolve("music"),
						origPath.resolve("book").resolve("music"));
			} catch (FileAlreadyExistsException e) {
				publish("Refusing to move already existing music");
			}

			this.publish("Extracting NPK...");
			try (InputStream npkIn = Files.newInputStream(dataPath)) {
				final NnpkFileReader r = new NnpkFileReader(npkIn);
				r.readAll();

				this.countFilesRecurse(r.getTableOfContents());

				final NnpkFileExtractor extract = new NnpkFileExtractor(npkIn);
				extract.setOutput(outputOrig);
				extract.extractAllFiles(r.getTableOfContents());
			}
			this.publish("Extracted NPK.");
			this.publish("Converting assets...");
			this.recurse(outputOrig);
			this.publish("Converting animations...");
			for (AnimationSources anim : anims) {
				this.convertAnimation(anim);
			}
			this.publish("Downloading external assets...");
			for (ExternalDownload down : downs) {
				this.downloadExternal(down);
			}
			this.publish("Done!");
		}

		private void downloadExternal(ExternalDownload down) throws IOException {
			this.publish("Downloading " + down.getUrl());
			Path outPath = LauncherMain.getAssetsPath().resolve(down.getOutPath());
			// mkdir parent directories
			Files.createDirectories(outPath.getParent());

			URL url = new URL(down.getUrl());

			try (OutputStream out = Files.newOutputStream(outPath); InputStream in = url.openStream()) {
				in.transferTo(out);
			}
			this.incProcessedCount();
		}

		private void convertAnimation(AnimationSources anim) throws IOException, ParseException {
			this.publish("Converting animation " + anim.getAnimationFile());
			try (InputStream inMesh = Files.newInputStream(LauncherMain.getAssetsPath().resolve(anim.getMeshFile()));
					InputStream inAnim =
							Files.newInputStream(LauncherMain.getAssetsPath().resolve(anim.getAnimationFile()));
					InputStream inScript =
							Files.newInputStream(LauncherMain.getAssetsPath().resolve(anim.getScriptFile()));
					OutputStream outGltfBuf = Files
						.newOutputStream(LauncherMain.getAssetsPath().resolve(anim.getOutputPath() + ".bin"))) {

				GLTFGenerator gen = new GLTFGenerator(outGltfBuf);

				IParser parser = new NOBParser();
				parser.setClassModel(this.model);
				parser.read(inScript);
				gen.addBones(parser.getCalls());

				gen.buildBasicScene("scene", gen.getGltf().getNodes().size());

				NvxFileReader mesh = new NvxFileReader(inMesh);
				mesh.readAll();
				gen.addMesh("skin", mesh.getTypes(), mesh.getVertices(), mesh.getTriangles(), 0);

				NaxFileReader nax = new NaxFileReader(inAnim);
				List<Curve> curves = nax.readAll();
				gen.addCurves(curves);

				gen.buildBuffer(Path.of(anim.getOutputPath() + ".bin").getFileName().toString());

				MAPPER.writeValue(
						Files.newOutputStream(LauncherMain.getAssetsPath().resolve(anim.getOutputPath() + ".gltf")),
						gen.getGltf());
			}
			this.incProcessedCount();
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
				break;
			case "ntx":
				this.publish("Converting texture " + file);
				this.processTexture(file, new File(outPath + ".ktx"));
				this.incProcessedCount();
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
			this.setProgress(100 * this.filesProcessed / this.filesToProcessCount);
		}

		private void unprocessableFile(final File file) {
			this.publish("Don't know how to process file " + file.getPath());
			file.delete();
		}

		@Override
		protected void done() {
			ExtractStep.this.ctx.setNextEnabled(true);
		}

		@Override
		protected void process(final List<String> chunks) {
			for (final String s : chunks) {
				ExtractStep.this.textArea.append(s);
				ExtractStep.this.textArea.append("\n");
			}
			ExtractStep.this.scroll.getVerticalScrollBar()
				.setValue(ExtractStep.this.scroll.getVerticalScrollBar().getMaximum());

			ExtractStep.this.ctx.setNextEnabled(false);
		}
	}
}
