package me.vinceh121.wanderer.launcher.wizard;

import javax.swing.JPanel;

public abstract class AbstractWizardStep extends JPanel {
	private static final long serialVersionUID = 1L;

	protected final FirstTimeWizardContext ctx;

	public AbstractWizardStep(final FirstTimeWizardContext ctx) {
		this.ctx = ctx;
	}

	public void onSwitchTo() {
	}

	public boolean canNextStep() {
		return true;
	}
}
