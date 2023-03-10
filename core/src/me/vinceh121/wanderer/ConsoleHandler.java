package me.vinceh121.wanderer;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ScriptableObject;

import com.badlogic.gdx.Gdx;

import me.vinceh121.wanderer.script.JsGame;
import me.vinceh121.wanderer.script.JsUtils;

public class ConsoleHandler implements AutoCloseable {
	private final Logger LOG = LogManager.getLogger(ConsoleHandler.class);
	private final Wanderer game;
	private final Terminal terminal;
	private final LineReader lineReader;
	private boolean closed = false;
	private Thread listenerThread;
	private Context jsContext;
	private ScriptableObject scope;

	public ConsoleHandler(Wanderer game) throws IOException {
		this.game = game;
		this.terminal = TerminalBuilder.builder()
			.color(true)
			.name("Wanderer")
			.system(true)
			.streams(System.in, System.out)
			.jansi(true)
			.build();
		this.lineReader = LineReaderBuilder.builder()
			.appName("Wanderer")
			.terminal(terminal)
			.history(new DefaultHistory())
			.build();
	}

	public void start() {
		this.listenerThread = new Thread(this::run, "Console");
		this.listenerThread.start();
	}

	private void run() {
		this.jsContext = ContextFactory.getGlobal().enterContext();
		this.scope = this.jsContext.initSafeStandardObjects();
		new JsGame(this.game).install(this.scope);
		fillConsoleScope(scope);
		while (!this.closed) {
			try {
				final String line = this.lineReader.readLine("Wanderer> ");
				Object res = this.jsContext.evaluateString(this.scope, line, "<stdin>", -1, null);
				System.out.println(ScriptRuntime.toString(res));
			} catch (EcmaError e) {
				LOG.error("", e);
			} catch (UserInterruptException e) {
				// ignore
			} catch (Exception e) {
				LOG.error("Unhandled exception in console", e);
			}
		}
	}

	@Override
	public void close() throws Exception {
		this.closed = true;
		this.terminal.close();
		this.listenerThread.interrupt();
	}

	private void fillConsoleScope(ScriptableObject scope) {
		ScriptManager.fillStoryScope(scope);

		JsUtils.install(scope, "exit", () -> Gdx.app.postRunnable(() -> this.game.dispose()));
		JsUtils.install(scope, "forceexit", () -> System.exit(-1));
	}
}
