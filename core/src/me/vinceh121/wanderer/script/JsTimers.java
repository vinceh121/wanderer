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
	private final Map<Integer, Interval> intervals = new Hashtable<>();
	private int nextId;

	public void install(final Scriptable scope) {
		JsUtils.install(scope, "setTimeout", this::setTimeout);
		JsUtils.install(scope, "clearTimeout", this::clearTimeout);
		
		JsUtils.install(scope, "setInterval", this::setInterval);
		JsUtils.install(scope, "clearInterval", this::clearInterval);
	}

	private Object setTimeout(final Context ctx, final Scriptable lscope, final Scriptable thisObj, final Object[] args) {
		/// XXX Note: unlike spec-compliant Javascript, the this value is properly kept.
		final Timeout timeout;

		if (args.length == 2) {
			timeout = new Timeout(ctx, lscope, thisObj, (Callable) args[0], ((Number) args[1]).floatValue());
		} else if (args.length > 2) {
			final Object[] timeoutArgs = new Object[args.length - 2];
			System.arraycopy(args, 2, timeoutArgs, 0, args.length - 2);
			timeout = new Timeout(ctx,
					lscope,
					thisObj,
					(Callable) args[0],
					((Number) args[1]).floatValue(),
					timeoutArgs);
		} else {
			throw new IllegalStateException("setTimeout() needs arguments");
		}
		int id = this.nextId++;
		this.timeouts.put(id, timeout);

		return id;
	}

	private Object clearTimeout(final Context lcx, final Scriptable lscope, final Scriptable thisObj, final Object[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException("clearTimeout() must have 1 argument");
		}
		this.timeouts.remove((int) args[0]);
		return Undefined.instance;
	}

	private Object setInterval(final Context ctx, final Scriptable lscope, final Scriptable thisObj, final Object[] args) {
		/// XXX Note: unlike spec-compliant Javascript, the this value is properly kept.
		final Interval interval;

		if (args.length == 2) {
			interval = new Interval(ctx, lscope, thisObj, (Callable) args[0], ((Number) args[1]).floatValue());
		} else if (args.length > 2) {
			final Object[] intervalArgs = new Object[args.length - 2];
			System.arraycopy(args, 2, intervalArgs, 0, args.length - 2);
			interval = new Interval(ctx,
					lscope,
					thisObj,
					(Callable) args[0],
					((Number) args[1]).floatValue(),
					intervalArgs);
		} else {
			throw new IllegalStateException("setInterval() needs arguments");
		}
		int id = this.nextId++;
		this.intervals.put(id, interval);

		return id;
	}
	
	private Object clearInterval(final Context lcx, final Scriptable lscope, final Scriptable thisObj, final Object[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException("clearInterval() must have 1 argument");
		}
		this.intervals.remove(((Number) args[0]).intValue());
		return Undefined.instance;
	}
	
	public void update(final float delta) {
		for (final int id : this.timeouts.keySet()) {
			final Timeout timeout = this.timeouts.get(id);
			timeout.timeRemaining -= (delta * 1000);
			if (timeout.timeRemaining < 0) {
				this.timeouts.remove(id);
				timeout.function.call(timeout.ctx, timeout.scope, timeout.thys, timeout.args);
			}
		}
		
		for (final int id : this.intervals.keySet()) {
			final Interval interval = this.intervals.get(id);
			interval.timeRemaining -= (delta * 1000);
			if (interval.timeRemaining < 0) {
				interval.function.call(interval.ctx, interval.scope, interval.thys, interval.args);
				interval.timeRemaining = interval.delay;
			}
		}
	}

	public Map<Integer, Timeout> getTimeouts() {
		return this.timeouts;
	}

	public static JsTimers getInstance() {
		return JsTimers.INSTANCE;
	}

	private static class Interval {
		private final Context ctx;
		private final Scriptable scope, thys;
		private final Callable function;
		private final Object[] args;
		private float delay;
		private float timeRemaining;

		public Interval(final Context ctx, final Scriptable scope, final Scriptable thys, final Callable function, final float delay) {
			this(ctx, scope, thys, function, delay, new Object[0]);
		}

		public Interval(final Context ctx, final Scriptable scope, final Scriptable thys, final Callable function, final float delay,
				final Object[] args) {
			this.ctx = ctx;
			this.scope = scope;
			this.thys = thys;
			this.function = function;
			this.delay = delay;
			this.timeRemaining = delay;
			this.args = args;
		}
	}
	
	private static class Timeout {
		private final Context ctx;
		private final Scriptable scope, thys;
		private final Callable function;
		private final Object[] args;
		private float timeRemaining;

		public Timeout(final Context ctx, final Scriptable scope, final Scriptable thys, final Callable function, final float timeRemaining) {
			this(ctx, scope, thys, function, timeRemaining, new Object[0]);
		}

		public Timeout(final Context ctx, final Scriptable scope, final Scriptable thys, final Callable function, final float timeRemaining,
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
