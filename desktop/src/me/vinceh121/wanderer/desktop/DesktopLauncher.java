package me.vinceh121.wanderer.desktop;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.Configuration;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.electronwill.nightconfig.core.CommentedConfig;

import me.vinceh121.wanderer.ApplicationMultiplexer;
import me.vinceh121.wanderer.MainMenu;
import me.vinceh121.wanderer.Preferences;
import me.vinceh121.wanderer.i18n.I18N;

public class DesktopLauncher {
	private static final Logger LOG = LogManager.getLogger(DesktopLauncher.class);

	public static void main(final String[] args) {
		try {
			main0(args);
		} catch (final Throwable t) {
			LOG.error("Unhandled error", t);
			System.exit(-1);
		}
	}

	private static void main0(final String[] args) {
		final Path configPath = getConfigPath();
		if (configPath == null) {
			LOG.error("Could not find proper config file path");
			Preferences.loadInMemory();
		} else {
			Preferences.loadPreferences(configPath);
		}
		final CommentedConfig prefs = Preferences.getPreferences();
		final boolean debug = prefs.getOrElse("debug", false);

		try {
			I18N.load(prefs.getOrElse("locale.ui", "en_UK"));
		} catch (final IOException e) {
			LOG.error("Failed to load UI locale", e);
		}

		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		Configuration.DEBUG.set(debug);
		config.setTitle("Wanderer");
		if (debug) {
			config.enableGLDebugOutput(debug, System.err);
		}
		// all default except for 4× anti aliasing
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, prefs.getIntOrElse("graphics.msaaSamples", 4));

		if (prefs.get("graphics.monitor") instanceof Integer) {
			config.setWindowedMode(512, 512);
		} else {
			Monitor monitor = getMonitor(prefs.<String>get("graphics.monitor"));
			config.setFullscreenMode(getDisplayMode(monitor, prefs.<String>get("graphics.resolution")));
		}

		ApplicationMultiplexer multiplexer = new ApplicationMultiplexer();
		multiplexer.setDelegate(new MainMenu(multiplexer));
		new WandererDesktopApplication(multiplexer, config);
	}

	private static Monitor getMonitor(String name) { // FIXME duplicate code with GraphicsUtilities
		return Arrays.stream(Lwjgl3ApplicationConfiguration.getMonitors())
			.filter(m -> name.equals(m.name))
			.findFirst()
			.orElse(null);
	}

	private static DisplayMode getDisplayMode(Monitor monitor, String displayMode) { // FIXME duplicate code with
																						// GraphicsUtilities
		return Arrays.stream(Lwjgl3ApplicationConfiguration.getDisplayModes(monitor))
			.filter(d -> displayMode.equals(d.toString()))
			.findFirst()
			.orElse(null);
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
