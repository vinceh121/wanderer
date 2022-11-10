package me.vinceh121.wanderer.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public class JsConsole {
	public void install(final Scriptable scope) {
		Scriptable console = this.buildConsoleObject(scope);
		scope.put("console", scope, console);
	}

	public Scriptable buildConsoleObject(final Scriptable scope) {
		Scriptable console = new NativeObject();

		JsUtils.install(console, "log", this::log);
		JsUtils.install(console, "trace", this::trace);

		scope.put("console", scope, console);

		return console;
	}

	private Object log(Context lcx, Scriptable lscope, Scriptable thisObj, Object[] args) {
		System.out.println(this.buildOutput(args));
		return Undefined.instance;
	}

	private Object trace(Context lcx, Scriptable lscope, Scriptable thisObj, Object[] args) {
		Thread.dumpStack();
		return Undefined.instance;
	}

	private String buildOutput(Object[] args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			sb.append(String.valueOf(args[i]));
			if (i != args.length - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
}
