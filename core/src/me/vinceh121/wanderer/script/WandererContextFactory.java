package me.vinceh121.wanderer.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class WandererContextFactory extends ContextFactory {
	@Override
	protected boolean hasFeature(final Context cx, final int featureIndex) {
		switch (featureIndex) {
		case Context.FEATURE_ENABLE_JAVA_MAP_ACCESS:
			return true;
		}
		return super.hasFeature(cx, featureIndex);
	}
}
