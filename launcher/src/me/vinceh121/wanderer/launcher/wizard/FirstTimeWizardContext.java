package me.vinceh121.wanderer.launcher.wizard;

import java.nio.file.Path;
import java.util.function.Consumer;

import me.vinceh121.wanderer.launcher.data.DataNpkSum;
import me.vinceh121.wanderer.launcher.data.VoiceLineSum;

public class FirstTimeWizardContext {
	private Path dataPath;
	private DataNpkSum data;
	private VoiceLineSum voice;
	private Consumer<Boolean> setPreviousEnabled, setNextEnabled;

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

	public void onSetPreviousEnabled(Consumer<Boolean> setPreviousEnabled) {
		this.setPreviousEnabled = setPreviousEnabled;
	}

	public void setPreviousEnabled(boolean enabled) {
		if (this.setPreviousEnabled != null) {
			this.setPreviousEnabled.accept(enabled);
		}
	}

	public void onSetNextEnabled(Consumer<Boolean> setNextEnabled) {
		this.setNextEnabled = setNextEnabled;
	}
	
	public void setNextEnabled(boolean enabled) {
		if (this.setNextEnabled != null) {
			this.setNextEnabled.accept(enabled);
		}
	}
}
