package me.vinceh121.wanderer.launcher.wizard;

import java.nio.file.Path;

import me.vinceh121.wanderer.launcher.DataNpkSum;
import me.vinceh121.wanderer.launcher.VoiceLineSum;

public class FirstTimeWizardContext {
	private Path dataPath;
	private DataNpkSum data;
	private VoiceLineSum voice;

	public Path getDataPath() {
		return this.dataPath;
	}

	public void setDataPath(final Path dataPath) {
		this.dataPath = dataPath;
	}

	public DataNpkSum getData() {
		return this.data;
	}

	public void setData(final DataNpkSum data) {
		this.data = data;
	}

	public VoiceLineSum getVoice() {
		return this.voice;
	}

	public void setVoice(final VoiceLineSum voice) {
		this.voice = voice;
	}
}
