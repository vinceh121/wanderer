package me.vinceh121.wanderer.launcher.wizard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class FirstTimeWizard extends JDialog {
	private static final long serialVersionUID = 1L;
	private final FirstTimeWizardContext ctx;
	private final List<AbstractWizardStep> steps;
	private final JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
	private final JButton btnNext = new JButton("Next"), btnPrev = new JButton("Previous"),
			btnFinish = new JButton("Finish");
	private AbstractWizardStep currentPanel;
	private int currentStep;

	public FirstTimeWizard(final FirstTimeWizardContext ctx, final List<AbstractWizardStep> steps) {
		this.ctx = ctx;
		this.steps = steps;

		this.setSize(500, 500);
		this.setTitle("Wanderer installation wizard");
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setModal(true);
		this.add(this.pnlButtons, BorderLayout.SOUTH);

		this.btnPrev.setEnabled(false);
		this.btnPrev.addActionListener(e -> this.previous());
		this.pnlButtons.add(this.btnPrev);

		this.btnNext.addActionListener(e -> this.next());
		this.pnlButtons.add(this.btnNext);

		this.btnFinish.setVisible(false);
		this.btnFinish.addActionListener(e -> this.dispose());
		this.pnlButtons.add(this.btnFinish);

		this.currentPanel = this.steps.get(0);
		this.add(this.currentPanel, BorderLayout.CENTER);

		this.ctx.onSetNextEnabled(this.btnNext::setEnabled);
		this.ctx.onSetPreviousEnabled(this.btnPrev::setEnabled);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int res = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to cancel the ongoing installation?",
						"Cancel confirmation",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (res == JOptionPane.YES_OPTION) {
					FirstTimeWizard.this.dispose();
				}
			}
		});
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
		if (this.currentStep != this.steps.size() - 1) {
			this.btnNext.setEnabled(true);
		} else {
			this.btnNext.setVisible(false);
			this.btnFinish.setVisible(true);
		}

		this.remove(this.currentPanel);
		this.currentPanel = this.steps.get(this.currentStep);
		this.currentPanel.onSwitchTo();
		this.add(this.currentPanel, BorderLayout.CENTER);
		this.validate();
		this.repaint();
	}
}
