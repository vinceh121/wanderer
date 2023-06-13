package me.vinceh121.wanderer.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import me.vinceh121.wanderer.i18n.I18N;

public final class JsI18N {
	public static void install(Scriptable scope) {
		JsUtils.install(scope, "gettext", (Context cx, Scriptable innerScope, Scriptable thisObj, Object[] args) -> {
			if (args.length == 0) {
				throw new IllegalArgumentException("gettext() function expects at least 1 argument");
			} else if (args.length == 1) {
				return I18N.gettext((String) args[0]);
			} else {
				Object[] formatParams = new Object[args.length - 1];
				// strip first string argument
				System.arraycopy(args, 1, formatParams, 0, formatParams.length);
				return I18N.gettext((String) args[0], formatParams);
			}
		});
	}
}
