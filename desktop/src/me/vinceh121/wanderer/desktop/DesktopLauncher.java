package me.vinceh121.wanderer.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.audio.Lwjgl3Audio;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.desktop.audio.OpenAL3DAudio;
import me.vinceh121.wanderer.desktop.audio.OpenALException;

public class DesktopLauncher {
	public static void main(final String[] arg) {
		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.enableGLDebugOutput(true, System.out);
		config.useOpenGL3(true, 3, 2);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4); // all default except for 4× anti aliasing
		new Lwjgl3Application(new Wanderer(), config) {
			@Override
			public Lwjgl3Audio createAudio(final Lwjgl3ApplicationConfiguration config) {
				try {
					return new OpenAL3DAudio();
				} catch (final OpenALException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}
