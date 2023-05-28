package me.vinceh121.wanderer.launcher.wizard;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;

import javax.swing.JFormattedTextField.AbstractFormatter;

public class FileFormatter extends AbstractFormatter {
	private static final long serialVersionUID = 1L;

	@Override
	public Object stringToValue(final String text) throws ParseException {
		final Path path = Path.of(text);
		if (Files.notExists(path)) {
			throw new ParseException("File not found", 0);
		}
		if (!path.getFileName().toString().endsWith(".npk")) {
			throw new ParseException("Not a .npk file", 0);
		}
		return path;
	}

	@Override
	public String valueToString(final Object value) throws ParseException {
		if (value == null) {
			return "";
		}
		return value.toString();
		/*
		 * return ((Path) value).toAbsolutePath().toString();
		 */
	}

}
