package me.vinceh121.wanderer.launcher;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

		final JMenuBar menu = new JMenuBar();
		this.setJMenuBar(menu);

		final JMenu mnHelp = new JMenu("Help");
		menu.add(mnHelp);

		final JMenuItem mntRerunWizard = new JMenuItem("Rerun installation wizard");
		mntRerunWizard.addActionListener(e -> LauncherMain.runWizard());
		mnHelp.add(mntRerunWizard);

		final JMenuItem mntInstallDetails = new JMenuItem("Installation details");
		mntInstallDetails.addActionListener(e -> {
			try {
				new InstallationInformationDialog().setVisible(true);
			} catch (final IOException e1) {
				LauncherFrame.LOG.error("Error loading installation details", e1);
				JOptionPane.showMessageDialog(null, "Error loading installation details " + e1);
			}
		});
		mnHelp.add(mntInstallDetails);

		final JMenuItem mntAbout = new JMenuItem("About");
		mntAbout.addActionListener(e -> new AboutDialog().setVisible(true));
		mnHelp.add(mntAbout);
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
		new Thread(this::start0, "MainGameThread").start();
	}

	private void start0() {
		this.started = true;
		try {
			final ProcessBuilder processBuilder;

			if ("package".equals(System.getProperty("wanderer.launcher"))) {
				final Path launcherPath = Path.of(System.getProperty("jpackage.app-path"));
				final Path desktopPath =
						launcherPath.getParent().resolve(SystemUtils.IS_OS_WINDOWS ? "Desktop.exe" : "Desktop");

				processBuilder = new ProcessBuilder(desktopPath.toString());
			} else {
				final Path home = Path.of(System.getProperty("java.home"));
				final Path java;

				if (SystemUtils.IS_OS_WINDOWS) {
					java = home.resolve("bin").resolve("javaw.exe");
				} else if (SystemUtils.IS_OS_UNIX) {
					java = home.resolve("bin").resolve("java");
				} else {
					throw new IllegalStateException("Don't know how to fetch Java execultable for your OS");
				}

				processBuilder = new ProcessBuilder(java.toAbsolutePath().toString(), "-jar", "desktop.jar");
			}

			processBuilder.inheritIO();
			processBuilder.directory(new File(System.getProperty("user.dir")));
			processBuilder.environment().remove("_JPACKAGE_LAUNCHER");

			final Process process = processBuilder.start();

			LauncherFrame.LOG.info("Executing {}", process.info().commandLine().orElse(null));

			final int status = process.waitFor(); // TODO error code handling
			LauncherFrame.LOG.info("Wanderer exited with status {}", status);
		} catch (final Throwable t) {
			LauncherFrame.LOG.error("Unexpected error", t);
			JOptionPane.showMessageDialog(null, "Unexpected error in Wanderer: " + t);
		}
		this.started = false;
		this.setVisible(true);
	}
}
