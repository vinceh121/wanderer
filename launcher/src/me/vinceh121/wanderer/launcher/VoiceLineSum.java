package me.vinceh121.wanderer.launcher;

import java.util.Locale;

/**
 * To determine the selected locale of the PN assets, we use the sha256 sum of
 * the first voice line of the game (c00p01spr01.wav) to discriminate. See
 * resource c00p01spr01Sums.json for a {@code sha256sum => VoiceLineSum}
 * dictionary.
 */
public class VoiceLineSum {
	private String locale;

	public String getLocale() {
		return this.locale;
	}

	public void setLocale(final String locale) {
		this.locale = locale;
	}

	public Locale getJavaLocale() {
		return new Locale(this.getLocale());
	}

	@Override
	public String toString() {
		return "VoiceLineSum [locale=" + this.locale + "]";
	}
}
