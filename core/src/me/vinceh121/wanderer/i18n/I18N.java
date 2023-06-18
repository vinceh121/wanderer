package me.vinceh121.wanderer.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class I18N {
	private static final Map<String, String> STRINGS = new HashMap<>();

	public static String gettext(final String msgid) {
		final String str = I18N.STRINGS.get(msgid);
		return str != null ? str : msgid;
	}

	public static String gettext(final String msgid, final Object... args) {
		return String.format(I18N.gettext(msgid), args);
	}

	public static void load(final String locale) throws IOException {
		I18N.STRINGS.clear();
		try (final InputStream in = I18N.class.getClassLoader().getResourceAsStream("i18n/" + locale + ".properties")) {

			if (in == null) {
				throw new IOException("Locale file for " + locale + " not found");
			}

			final Properties props = new Properties();
			props.load(in);

			for (final Entry<Object, Object> e : props.entrySet()) {
				I18N.STRINGS.put((String) e.getKey(), (String) e.getValue());
			}
		}
	}
}
