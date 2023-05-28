package me.vinceh121.wanderer.launcher.wizard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class FirstTimeWizard extends JDialog {
	private static final long serialVersionUID = 1L;
	private final FirstTimeWizardContext ctx;
	private final List<AbstractWizardStep> steps;
	private final JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
	private final JButton btnNext = new JButton("Next"), btnPrev = new JButton("Previous");
	private AbstractWizardStep currentPanel;
	private int currentStep;

	public FirstTimeWizard(final FirstTimeWizardContext ctx, final List<AbstractWizardStep> steps) {
		this.ctx = ctx;
		this.steps = steps;
		this.setSize(500, 500);
		this.setTitle("Wanderer installation wizard");
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.add(this.pnlButtons, BorderLayout.SOUTH);

		this.btnPrev.setEnabled(false);
		this.btnPrev.addActionListener(e -> this.previous());
		this.pnlButtons.add(this.btnPrev);

		this.btnNext.addActionListener(e -> this.next());
		this.pnlButtons.add(this.btnNext);

		this.currentPanel = this.steps.get(0);
		this.add(this.currentPanel, BorderLayout.CENTER);
	}

	private void next() {
		if (this.currentPanel.canNextStep()) {
			this.currentStep++;
			this.updateStep();
		}
	}

	private void previous() {
		this.currentStep--;
		this.updateStep();
	}

	private void updateStep() {
		this.btnPrev.setEnabled(this.currentStep != 0);
		this.btnNext.setEnabled(this.currentStep != this.steps.size() - 1);

		this.remove(this.currentPanel);
		this.currentPanel = this.steps.get(this.currentStep);
		this.currentPanel.onSwitchTo();
		this.add(this.currentPanel, BorderLayout.CENTER);
		this.repaint();
		this.currentPanel.invalidate();
	}
}
