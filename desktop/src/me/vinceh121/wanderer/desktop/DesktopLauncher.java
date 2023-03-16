package me.vinceh121.wanderer.desktop;

import java.nio.file.Path;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.Configuration;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import me.vinceh121.wanderer.ApplicationMultiplexer;
import me.vinceh121.wanderer.Preferences;
import me.vinceh121.wanderer.StoryWanderer;

public class DesktopLauncher {
	private static final Logger LOG = LogManager.getLogger(DesktopLauncher.class);

	public static void main(final String[] arg) {
		final Path configPath = getConfigPath();
		if (configPath == null) {
			LOG.error("Could not find proper config file path");
			Preferences.loadInMemory();
		} else {
			Preferences.loadPreferences(configPath);
		}
		final boolean debug = Preferences.getPreferences().getOrElse("debug", false);

		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		Configuration.DEBUG.set(debug);
		config.setTitle("Wanderer");
		config.enableGLDebugOutput(debug, System.err);
		config.setBackBufferConfig(8,
				8,
				8,
				8,
				16,
				0,
				Preferences.getPreferences().getIntOrElse("graphics.msaaSamples", 4));// all default except for 4× anti
																						// aliasing
		try {
			new WandererDesktopApplication(new ApplicationMultiplexer(new StoryWanderer()), config);
		} catch (Exception e) {
			LOG.error("Uncaught braught back up to main!", e);
			System.exit(-1);
		}
	}

	private static Path getConfigPath() {
		if (SystemUtils.IS_OS_WINDOWS) {
			return Path.of(System.getenv("APPDATA"), "wanderer", "settings.toml");
		} else if (SystemUtils.IS_OS_MAC) {
			return Path
				.of(System.getProperty("user.home"), "Library", "Application Support", "wanderer", "settings.toml");
		} else if (SystemUtils.IS_OS_UNIX) {
			return Path.of(System.getProperty("user.home"), ".config", "wanderer", "settings.toml");
		} else {
			return null;
		}
	}
}
