package me.vinceh121.wanderer.launcher;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent.EventType;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public AboutDialog() {
		this.setTitle("About Wanderer");

		final JEditorPane txt = new JEditorPane();
		txt.setEditable(false);
		txt.addHyperlinkListener(e -> {
			try {
				if (e.getEventType() == EventType.ACTIVATED) {
					Desktop.getDesktop().browse(e.getURL().toURI());
				}
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		txt.setContentType("text/html");
		txt.setText(
				"<h1>Wanderer</h1>" + "<p>A reimplementation of the 2002 video game Project Nomads by Radon Labs</p>"
						+ "<p><a href='https://github.com/vinceh121/wanderer'>Source code</a></p>"
						+ "<p>Licensed under the GNU AGPL V3</p>");

		this.add(txt);

		this.pack();
		this.setLocationRelativeTo(null);
	}
}
