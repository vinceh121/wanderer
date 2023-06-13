package me.vinceh121.wanderer.i18n;

public class I18N {
	public static String gettext(String msgid) {
		return msgid;
	}
	
	public static String gettext(String msgid, Object... args) {
		return String.format(msgid, args);
	}
}
