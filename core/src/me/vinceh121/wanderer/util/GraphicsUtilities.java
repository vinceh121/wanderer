package me.vinceh121.wanderer.util;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.electronwill.nightconfig.core.CommentedConfig;

import me.vinceh121.wanderer.Preferences;

public class GraphicsUtilities {
	public static void setFromPreferences() {
		final CommentedConfig p = Preferences.getPreferences();

		if (!(p.get("graphics.monitor") instanceof Object)) {
			Gdx.graphics.setWindowedMode(512, 512);
		} else {
			final Monitor m =
					GraphicsUtilities.getMonitor(Gdx.graphics.getMonitors(), p.<String>get("graphics.monitor"));
			Gdx.graphics.setFullscreenMode(GraphicsUtilities.getDisplayMode(Gdx.graphics.getDisplayModes(m),
					p.<String>get("graphics.resolution")));
		}

		Gdx.graphics.setVSync(p.<Boolean>get("graphics.vsync"));
	}

	public static Monitor getMonitor(final Monitor[] monitors, final String name) {
		return Arrays.stream(monitors).filter(m -> name.equals(m.name)).findFirst().orElse(null);
	}

	public static DisplayMode getDisplayMode(final DisplayMode[] modes, final String displayMode) {
		return Arrays.stream(modes).filter(d -> displayMode.equals(d.toString())).findFirst().orElse(null);
	}
}
