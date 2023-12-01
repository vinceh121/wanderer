package me.vinceh121.wanderer.desktop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.ApplicationLogger;

public class Log4jApplicationLogger implements ApplicationLogger {
	private Logger get(final String tag) {
		return LogManager.getLogger("Gdx-" + tag);
	}

	@Override
	public void log(final String tag, final String message) {
		this.get(tag).info(message);
	}

	@Override
	public void log(final String tag, final String message, final Throwable exception) {
		this.get(tag).info(message, exception);
	}

	@Override
	public void error(final String tag, final String message) {
		this.get(tag).error(message);
	}

	@Override
	public void error(final String tag, final String message, final Throwable exception) {
		this.get(tag).error(message, exception);
	}

	@Override
	public void debug(final String tag, final String message) {
		this.get(tag).debug(message);
	}

	@Override
	public void debug(final String tag, final String message, final Throwable exception) {
		this.get(tag).debug(message, exception);
	}
}
