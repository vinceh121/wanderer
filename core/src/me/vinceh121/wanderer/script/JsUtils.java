package me.vinceh121.wanderer.script;

import java.util.function.Consumer;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.LambdaFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public final class JsUtils {
	public static void install(final Scriptable scope, final String propName, final Consumer<Object[]> function) {
		install(scope, propName, (Context cx, Scriptable s, Scriptable thisObj, Object[] args) -> {
			function.accept(args);
			return Undefined.instance;
		});
	}

	public static void install(final Scriptable scope, final String propName, final Callable function) {
		// the bullshit arity of 1 should be fine, seems it's only used for decompiling
		final LambdaFunction jsFunc = new LambdaFunction(scope, 1, function);
		scope.put(propName, scope, jsFunc);
	}
}
