package me.vinceh121.wanderer.launcher;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class LauncherFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public LauncherFrame() {
		this.setSize(400, 600);
		this.setLocationRelativeTo(null);
		this.setTitle("Wanderer Launcher");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
