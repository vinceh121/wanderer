package me.vinceh121.wanderer.desktop.audio;

import org.lwjgl.openal.AL10;

public class OpenALException extends Exception {
	private static final long serialVersionUID = 4891244118761102268L;

	public OpenALException() {
	}

	public OpenALException(final int alErr) {
		this(AL10.alGetString(alErr));
	}

	public OpenALException(final String arg0, final Throwable arg1, final boolean arg2, final boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public OpenALException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	public OpenALException(final String arg0) {
		super(arg0);
	}

	public OpenALException(final Throwable arg0) {
		super(arg0);
	}
}
