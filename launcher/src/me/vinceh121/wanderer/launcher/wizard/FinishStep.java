package me.vinceh121.wanderer.launcher.wizard;

import javax.swing.JLabel;

public class FinishStep extends AbstractWizardStep {
	private static final long serialVersionUID = 1L;

	public FinishStep(FirstTimeWizardContext ctx) {
		super(ctx);
		// @formatter:off
		this.add(new JLabel("<html>"
				+ "<h1>All done!</h1>"
				+ "<p>The Wanderer installation is now complete! You can now start the game.</p>"
				+ "</html>"));
		// @formatter:on
	}
}
