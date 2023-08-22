package me.vinceh121.wanderer.launcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.formdev.flatlaf.util.SystemInfo;

import me.vinceh121.wanderer.launcher.pntheme.PnDarkLaf;
import me.vinceh121.wanderer.launcher.wizard.ConfirmStep;
import me.vinceh121.wanderer.launcher.wizard.DataSelectStep;
import me.vinceh121.wanderer.launcher.wizard.ExtractStep;
import me.vinceh121.wanderer.launcher.wizard.FinishStep;
import me.vinceh121.wanderer.launcher.wizard.FirstTimeWizard;
import me.vinceh121.wanderer.launcher.wizard.FirstTimeWizardContext;
import me.vinceh121.wanderer.launcher.wizard.WelcomeStep;

public class LauncherMain {
	public static void main(final String[] args) {
		if (SystemInfo.isLinux) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
		PnDarkLaf.setup();

		if (Files.notExists(LauncherMain.getAssetsPath())) {
			runWizard();
		}

		final LauncherFrame frame = new LauncherFrame();
		frame.setVisible(true);
	}

	public static void runWizard() {
		final FirstTimeWizardContext ctx = new FirstTimeWizardContext();
		final FirstTimeWizard wiz = new FirstTimeWizard(ctx,
				List.of(new WelcomeStep(ctx),
						new DataSelectStep(ctx),
						new ConfirmStep(ctx),
						new ExtractStep(ctx),
						new FinishStep(ctx)));
		wiz.setVisible(true);
	}
	
	public static Path getAssetsPath() {
		return Path.of(".", "assets");
	}
}
