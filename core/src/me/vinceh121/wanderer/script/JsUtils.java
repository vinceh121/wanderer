package me.vinceh121.wanderer.script;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.LambdaFunction;
import org.mozilla.javascript.Scriptable;

public final class JsUtils {
	public static void install(final Scriptable scope, final String propName, final Callable function) {
		// the bullshit arity of 1 should be fine, seems it's only used for decompiling
		final LambdaFunction jsFunc = new LambdaFunction(scope, 1, function);
		scope.put(propName, scope, jsFunc);
	}
}
