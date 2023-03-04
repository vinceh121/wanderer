package me.vinceh121.wanderer.desktop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.Configuration;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import me.vinceh121.wanderer.ApplicationMultiplexer;
import me.vinceh121.wanderer.StoryWanderer;

public class DesktopLauncher {
	private static final Logger LOG = LogManager.getLogger(DesktopLauncher.class);

	public static void main(final String[] arg) {
		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		Configuration.DEBUG.set(true);
		config.setTitle("Wanderer");
		config.enableGLDebugOutput(true, System.err);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4); // all default except for 4× anti aliasing
		try {
			new WandererDesktopApplication(new ApplicationMultiplexer(new StoryWanderer()), config);
		} catch (Exception e) {
			LOG.error("Uncaught braught back up to main!", e);
			System.exit(-1);
		}
	}
}
