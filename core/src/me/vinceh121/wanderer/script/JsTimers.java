package me.vinceh121.wanderer.script;

import java.util.Hashtable;
import java.util.Map;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public class JsTimers {
	private static final JsTimers INSTANCE = new JsTimers();
	private final Map<Integer, Timeout> timeouts = new Hashtable<>();
	private int nextId;

	public void install(final Scriptable scope) {
		JsUtils.install(scope, "setTimeout", this::setTimeout);
		JsUtils.install(scope, "clearTimeout", this::clearTimeout);
	}

	private Object setTimeout(final Context ctx, final Scriptable lscope, final Scriptable thisObj, final Object[] args) {
		/// XXX Note: unlike spec-compliant Javascript, the this value is properly kept.
		final Timeout timeout;

		if (args.length == 2) {
			timeout = new Timeout(ctx, lscope, thisObj, (Callable) args[0], ((Number) args[1]).longValue());
		} else if (args.length > 2) {
			final Object[] timeoutArgs = new Object[args.length - 2];
			System.arraycopy(args, 2, timeoutArgs, 0, args.length - 2);
			timeout = new Timeout(ctx,
					lscope,
					thisObj,
					(Callable) args[0],
					((Number) args[1]).longValue(),
					timeoutArgs);
		} else {
			throw new IllegalStateException("setTimeout() needs arguments");
		}
		this.timeouts.put(this.nextId++, timeout);

		return this.nextId;
	}

	private Object clearTimeout(final Context lcx, final Scriptable lscope, final Scriptable thisObj, final Object[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException("clearTimeout() must have 1 argument");
		}
		this.timeouts.remove((int) args[0]);
		return Undefined.instance;
	}

	public void update(final float delta) {
		for (final int id : this.timeouts.keySet()) {
			final Timeout t = this.timeouts.get(id);
			t.timeRemaining -= (long) (delta * 1000);
			if (t.timeRemaining < 0) {
				this.timeouts.remove(id);
				t.function.call(t.ctx, t.scope, t.thys, t.args);
			}
		}
	}

	public Map<Integer, Timeout> getTimeouts() {
		return this.timeouts;
	}

	public static JsTimers getInstance() {
		return JsTimers.INSTANCE;
	}

	private static class Timeout {
		private final Context ctx;
		private final Scriptable scope, thys;
		private final Callable function;
		private final Object[] args;
		private long timeRemaining;

		public Timeout(final Context ctx, final Scriptable scope, final Scriptable thys, final Callable function, final long timeRemaining) {
			this(ctx, scope, thys, function, timeRemaining, new Object[0]);
		}

		public Timeout(final Context ctx, final Scriptable scope, final Scriptable thys, final Callable function, final long timeRemaining,
				final Object[] args) {
			this.ctx = ctx;
			this.scope = scope;
			this.thys = thys;
			this.function = function;
			this.timeRemaining = timeRemaining;
			this.args = args;
		}
	}
}
