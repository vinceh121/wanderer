package me.vinceh121.wanderer.launcher;

import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.vinceh121.wanderer.launcher.data.VoiceLineSum;

public class InstallationInformationDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public InstallationInformationDialog() throws StreamReadException, DatabindException, IOException {
		final ObjectMapper mapper = new ObjectMapper();

		final Set<Locale> available = mapper.readValue(
				this.getClass()
					.getClassLoader()
					.getResourceAsStream("me/vinceh121/wanderer/launcher/c00p01spr01Sums.json"),
				new TypeReference<Map<String, VoiceLineSum>>() {
				})
			.values()
			.stream()
			.map(VoiceLineSum::getJavaLocale)
			.collect(Collectors.toUnmodifiableSet());

		final Set<Locale> translations = Files.list(LauncherMain.getAssetsPath().resolve(Path.of("orig", "book")))
			.map(Path::getFileName)
			.map(Path::toString)
			.filter(n -> !"music".equals(n))
			.map(Locale::new)
			.collect(Collectors.toUnmodifiableSet());

		this.setLayout(new GridLayout(available.size() + 1, 1));

		final JLabel lblVoiceLang = new JLabel("Voice languages");
		lblVoiceLang.setFont(lblVoiceLang.getFont().deriveFont(Font.BOLD, 16));
		this.add(lblVoiceLang);

		for (final Locale a : available) {
			final JCheckBox chkbx = new JCheckBox();
			chkbx.setEnabled(false);
			chkbx.setSelected(translations.contains(a));
			chkbx.setText(a.getDisplayLanguage());

			this.add(chkbx);
		}

		this.setTitle("Installation languages");
		this.pack();
		this.setLocationRelativeTo(null);
	}
}
