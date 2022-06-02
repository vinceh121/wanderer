package me.vinceh121.wanderer.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import me.vinceh121.wanderer.Wanderer;

public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.enableGLDebugOutput(true, System.out);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4); // all default except for 4× anti aliasing
		new Lwjgl3Application(new Wanderer(), config);
	}
}
