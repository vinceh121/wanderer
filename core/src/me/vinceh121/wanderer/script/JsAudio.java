package me.vinceh121.wanderer.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;

import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.platform.audio.SoundEmitter3D;

public final class JsAudio {
	private static final Logger LOG = new Logger("JsAudio");

	public static void install(Scriptable scope) {
		JsUtils.install(scope, "play", JsAudio::play);
	}

	private static Object play(Context lcx, Scriptable lscope, Scriptable thisObj, Object[] args) {
		final SoundEmitter3D source;
		if (args.length == 0) {
			throw new IllegalArgumentException("Can't invoke play() with no arguments");
		} else if (args.length > 0) {
			final String file = (String) args[0];

			if (!WandererConstants.ASSET_MANAGER.isLoaded(file)) {
				LOG.error("Hot-loading sound " + file);
				WandererConstants.ASSET_MANAGER.load(file, Sound3D.class);
				WandererConstants.ASSET_MANAGER.finishLoadingAsset(file);
			}

			final Sound3D sound = WandererConstants.ASSET_MANAGER.get(file, Sound3D.class);

			if (args.length == 1) {
				source = sound.playGeneral();
			} else if (args.length > 1) {
				source = sound.playSource3D();
				source.setPosition((Vector3) args[1]);
			} else {
				throw new IllegalArgumentException("This shouldn't happen, you win the game.");
			}
		} else {
			throw new IllegalArgumentException("Too many arguments to play()");
		}
		return source;
	}
}
