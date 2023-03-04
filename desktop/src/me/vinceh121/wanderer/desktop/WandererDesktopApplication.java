package me.vinceh121.wanderer.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.audio.Lwjgl3Audio;

import me.vinceh121.wanderer.desktop.audio.OpenAL3DAudio;
import me.vinceh121.wanderer.desktop.audio.OpenALException;

public class WandererDesktopApplication extends Lwjgl3Application {

	public WandererDesktopApplication(ApplicationListener listener, Lwjgl3ApplicationConfiguration config) {
		super(listener, config);
		// the loop starts in the constructor, so we have to hijack createAudio or
		// another setup method in order to override certain behavior
	}

	@Override
	public Lwjgl3Audio createAudio(final Lwjgl3ApplicationConfiguration config) {
		this.setApplicationLogger(new Log4jApplicationLogger());

		try {
			return new OpenAL3DAudio();
		} catch (final OpenALException e) {
			throw new RuntimeException(e);
		}
	}

}
