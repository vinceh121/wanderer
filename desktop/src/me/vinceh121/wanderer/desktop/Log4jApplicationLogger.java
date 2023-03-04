package me.vinceh121.wanderer.desktop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.ApplicationLogger;

public class Log4jApplicationLogger implements ApplicationLogger {
	private Logger get(String tag) {
		return LogManager.getLogger("Gdx-" + tag);
	}

	@Override
	public void log(String tag, String message) {
		get(tag).info(message);
	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		get(tag).info(message, exception);
	}

	@Override
	public void error(String tag, String message) {
		get(tag).error(message);
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		get(tag).error(message, exception);
	}

	@Override
	public void debug(String tag, String message) {
		get(tag).debug(message);
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		get(tag).debug(message, exception);
	}
}
