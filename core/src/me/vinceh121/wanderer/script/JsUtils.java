package me.vinceh121.wanderer.script;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.LambdaFunction;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.commonjs.module.ModuleScope;

public final class JsUtils {

	@SuppressWarnings("unchecked")
	public static <T, U> void install(final Scriptable scope, final String propName, final BiConsumer<T, U> function) {
		JsUtils.install(scope, propName, (final Context cx, final Scriptable s, final Scriptable thisObj, final Object[] args) -> {
			if (args.length < 1) {
				function.accept(null, null);
			} else {
				function.accept((T) JsUtils.maybeUnwrap(args[0]), (U) JsUtils.maybeUnwrap(args[1]));
			}
			return Undefined.instance;
		});
	}

	@SuppressWarnings("unchecked")
	public static <T> void install(final Scriptable scope, final String propName, final Consumer<T> function) {
		JsUtils.install(scope, propName, (final Context cx, final Scriptable s, final Scriptable thisObj, final Object[] args) -> {
			if (args.length < 1) {
				function.accept(null);
			} else {
				function.accept((T) JsUtils.maybeUnwrap(args[0]));
			}
			return Undefined.instance;
		});
	}

	@SuppressWarnings("unchecked")
	public static <T, R> void install(final Scriptable scope, final String propName, final Function<T, R> function) {
		JsUtils.install(scope, propName, (final Context cx, final Scriptable s, final Scriptable thisObj, final Object[] args) -> {
			if (args.length < 1) {
				return function.apply(null);
			} else {
				return function.apply((T) JsUtils.maybeUnwrap(args[0]));
			}
		});
	}

	public static <T> void install(final Scriptable scope, final String propName, final Supplier<T> function) {
		JsUtils.install(scope, propName, (final Context cx, final Scriptable s, final Scriptable thisObj, final Object[] args) -> function.get());
	}

	public static void install(final Scriptable scope, final String propName, final Runnable function) {
		JsUtils.install(scope, propName, (final Context cx, final Scriptable s, final Scriptable thisObj, final Object[] args) -> {
			function.run();
			return Undefined.instance;
		});
	}

	public static void install(final Scriptable scope, final String propName, final Callable function) {
		// the bullshit arity of 1 should be fine, seems it's only used for decompiling
		final LambdaFunction jsFunc = new LambdaFunction(scope, 1, function);
		scope.put(propName, scope, jsFunc);
	}

	public static Object maybeUnwrap(final Object in) {
		if (in instanceof NativeJavaObject) {
			return ((NativeJavaObject) in).unwrap();
		} else {
			return in;
		}
	}

	public static ModuleScope getModuleScope(final Scriptable scope) {
		if (scope instanceof ModuleScope) {
			return (ModuleScope) scope;
		} else if (scope.getParentScope() != null) {
			return JsUtils.getModuleScope(scope.getParentScope());
		} else {
			return null;
		}
	}
}
