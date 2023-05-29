package me.vinceh121.wanderer.launcher.wizard;

import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.vinceh121.wanderer.launcher.DataNpkSum;
import me.vinceh121.wanderer.launcher.VoiceLineSum;

public class ConfirmStep extends AbstractWizardStep {
	private static final long serialVersionUID = 1L;
	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
	private final JLabel lbl = new JLabel();
	private DataNpkSum data;
	private VoiceLineSum voice;

	public ConfirmStep(final FirstTimeWizardContext ctx) {
		super(ctx);
		this.setLayout(new BorderLayout());
		this.add(this.lbl, BorderLayout.CENTER);
	}

	@Override
	public boolean canNextStep() {
		if (!this.isValidSums()) {
			JOptionPane.showMessageDialog(null,
					"Cannot continue for the reasons displayed",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	@Override
	public void onSwitchTo() {
		new SumCheck().execute();
	}

	private boolean isValidSums() {
		return this.voice != null && (this.data == null || this.data.isValid());
	}

	private class SumCheck extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			final ObjectMapper mapper = new ObjectMapper();
			final Map<String, DataNpkSum> dataSums = mapper.readValue(
					this.getClass().getClassLoader().getResourceAsStream("me/vinceh121/wanderer/launcher/dataSums.json"),
					new TypeReference<Map<String, DataNpkSum>>() {
					});
			final Map<String, VoiceLineSum> voiceSums = mapper.readValue(
					this.getClass().getClassLoader()
						.getResourceAsStream("me/vinceh121/wanderer/launcher/c00p01spr01Sums.json"),
					new TypeReference<Map<String, VoiceLineSum>>() {
					});

			final String dataHash = ConfirmStep.fileSha256Sum(ConfirmStep.this.ctx.getDataPath());
			final String voiceHash = ConfirmStep.fileSha256Sum(ConfirmStep.this.ctx.getDataPath()
				.resolveSibling(Path.of("book", "chapter00", "part01", "sound", "c00p01spr01.wav")));

			ConfirmStep.this.data = dataSums.get(dataHash);
			ConfirmStep.this.voice = voiceSums.get(voiceHash);

			return null;
		}

		@Override
		protected void done() {
			if (ConfirmStep.this.isValidSums()) {
				ConfirmStep.this.lbl.setText("<html>Going to install assets from Project Nomads <b>"
						+ ConfirmStep.this.voice.getJavaLocale().getDisplayLanguage() + "</b></html>");
			} else {
				final StringBuilder txt = new StringBuilder();
				txt.append("<html>Cannot install assets from this Project Nomads installation because:<ul>");
				if (ConfirmStep.this.data == null) {
					txt.append("<li>The data.npk is not recognized</li>");
				} else {
					if (ConfirmStep.this.data.isDemo()) {
						txt.append("<li>The demo version does not contain all assets</li>");
					}
				}
				if (ConfirmStep.this.voice == null) {
					txt.append("<li>Unrecognized language</li>");
				}
				txt.append("</ul></html>");
				ConfirmStep.this.lbl.setText(txt.toString());
			}
		}
	}

	private static String fileSha256Sum(final Path p) throws IOException, NoSuchAlgorithmException {
		final byte[] sum = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(p));
		// https://stackoverflow.com/a/9855338
		final char[] hexChars = new char[sum.length * 2];
		for (int j = 0; j < sum.length; j++) {
			final int v = sum[j] & 0xFF;
			hexChars[j * 2] = ConfirmStep.HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = ConfirmStep.HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
}
