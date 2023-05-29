package me.vinceh121.wanderer.launcher.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.formdev.flatlaf.util.SystemInfo;

public class DataSelectStep extends AbstractWizardStep {
	private static final long serialVersionUID = 1L;
	private final JFormattedTextField txtPath = new JFormattedTextField(new FileFormatter());

	public DataSelectStep(final FirstTimeWizardContext ctx) {
		super(ctx);
		this.setLayout(new GridBagLayout());

		this.add(new JLabel("Please select the path to the \"data.npk\" file of your Project Nomads installation:"),
				new GridBagConstraints(0,
						0,
						2,
						1,
						0,
						0,
						GridBagConstraints.BASELINE_LEADING,
						GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0),
						0,
						0));

		this.add(this.txtPath,
				new GridBagConstraints(0,
						1,
						1,
						1,
						1,
						0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 0),
						0,
						0));

		final JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(e -> this.browse());
		this.add(btnBrowse,
				new GridBagConstraints(1,
						1,
						1,
						1,
						0,
						0,
						GridBagConstraints.BASELINE_LEADING,
						GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0),
						0,
						0));

		this.txtPath.setValue(DataSelectStep.getDefaultDataPath());
	}

	private void browse() {
		final JFileChooser fc;

		final File cur = new File(this.txtPath.getText());
		if (cur.exists() && cur.isDirectory()) {
			fc = new JFileChooser(cur);
		} else {
			fc = new JFileChooser();
		}

		fc.setMultiSelectionEnabled(false);
		FileFilter npkFilter = new FileFilter() {

			@Override
			public String getDescription() {
				return ".npk | NPK0 file";
			}

			@Override
			public boolean accept(final File f) {
				return f.isDirectory() || f.getName().endsWith(".npk");
			}
		};
		fc.addChoosableFileFilter(npkFilter);
		fc.setFileFilter(npkFilter);
		final int res = fc.showOpenDialog(null);
		if (res == JFileChooser.ERROR_OPTION) {
			JOptionPane.showMessageDialog(null,
					"Error while selecting file. No idea what this means.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} else if (res != JFileChooser.APPROVE_OPTION) {
			return;
		}

		this.txtPath.setValue(fc.getSelectedFile().toPath());
	}

	@Override
	public boolean canNextStep() {
		final Path path = Path.of(this.txtPath.getText());
		if (Files.notExists(path)) {
			JOptionPane
				.showMessageDialog(null, "File '" + path + "' does not exist", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		this.ctx.setDataPath(path);
		return true;
	}

	private static String getDefaultDataPath() {
		if (System.getenv("PN_DATA_NPK") != null) {
			return System.getenv("PN_DATA_NPK");
		} else if (SystemInfo.isWindows) {
			return "C:\\Program Files (x86)\\Project Nomads\\Run\\data.npk";
		} else if (SystemInfo.isLinux) {
			return System.getProperty("user.home") + "/.wine/drive_c/Program Files (x86)/Project Nomads/Run/data.npk";
		} else if (SystemInfo.isMacOS) {
			// TODO find out what does the MacOS edition looks like
		}
		return "";
	}
}
