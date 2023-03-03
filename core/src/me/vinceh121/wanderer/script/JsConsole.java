package me.vinceh121.wanderer.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.commonjs.module.ModuleScope;

import com.badlogic.gdx.Gdx;

public class JsConsole {
	public void install(final Scriptable scope) {
		final Scriptable console = this.buildConsoleObject(scope);
		scope.put("console", scope, console);
	}

	public Scriptable buildConsoleObject(final Scriptable scope) {
		final Scriptable console = new NativeObject();

		JsUtils.install(console, "log", this::log);
		JsUtils.install(console, "trace", this::trace);

		scope.put("console", scope, console);

		return console;
	}

	private Object log(final Context lcx, final Scriptable lscope, final Scriptable thisObj, final Object[] args) {
		String tag = "JS";
		if (lscope instanceof ModuleScope) {
			tag = ((ModuleScope) lscope).getUri().toString();
		}
		Gdx.app.log(tag, this.buildOutput(args));
		return Undefined.instance;
	}

	private Object trace(final Context lcx, final Scriptable lscope, final Scriptable thisObj, final Object[] args) {
		Thread.dumpStack();
		return Undefined.instance;
	}

	private String buildOutput(final Object[] args) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			sb.append(Context.toString(args[i]));
			if (i != args.length - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
}
