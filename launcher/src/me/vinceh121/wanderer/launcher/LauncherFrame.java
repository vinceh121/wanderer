package me.vinceh121.wanderer.launcher;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.vinceh121.wanderer.desktop.DesktopLauncher;

public class LauncherFrame extends JFrame {
	private static final Logger LOG = LogManager.getLogger(LauncherFrame.class);
	private static final long serialVersionUID = 1L;
	private boolean started;

	public LauncherFrame() {
		this.setSize(400, 600);
		this.setLocationRelativeTo(null);
		this.setTitle("Wanderer Launcher");
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				LauncherFrame.this.onClose();
			}
		});

		final JButton btnStart = new JButton("Start");
		btnStart.addActionListener(e -> this.start());
		this.add(btnStart);
	}

	private void onClose() {
		if (this.started) {
			final int status = JOptionPane.showConfirmDialog(null,
					"Wanderer is started. Are you sure you want to close it?",
					"Wanderer Launcher",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (status == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		} else {
			System.exit(0);
		}
	}

	public void start() {
		this.setVisible(false);
		new Thread(() -> {
			this.started = true;
			try {
				DesktopLauncher.main(null);
			} catch (Throwable t) {
				LOG.error("Unexpected error", t);
				JOptionPane.showMessageDialog(null, "Unexpected error in Wanderer: " + t);
			}
			this.started = false;
			this.setVisible(true);
			LOG.info("Wanderer terminated");
		}, "MainGameThread").start();
	}
}
