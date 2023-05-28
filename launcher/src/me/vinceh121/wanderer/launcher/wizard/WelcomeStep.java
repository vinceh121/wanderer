package me.vinceh121.wanderer.launcher.wizard;

import javax.swing.JLabel;

public class WelcomeStep extends AbstractWizardStep {
	private static final long serialVersionUID = 1L;

	public WelcomeStep(final FirstTimeWizardContext ctx) {
		super(ctx);
		// @formatter:off
		final JLabel lbl = new JLabel("<html>"
				+ "<h1>Welcome to the Wanderer setup wizard</h1>"
				+ "<p>This wizard will guide you through the installation process.</p>"
				+ "<p>Click on Next when ready to continue.</p>"
				+ "</html>");
		// @formatter:on
		this.add(lbl);
	}
}
