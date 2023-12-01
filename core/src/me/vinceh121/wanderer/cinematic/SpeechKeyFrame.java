package me.vinceh121.wanderer.cinematic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.vinceh121.wanderer.Preferences;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.platform.audio.SoundEmitter3D;

public class SpeechKeyFrame extends ActionKeyFrame {
	private static final Logger LOG = LogManager.getLogger(SpeechKeyFrame.class);
	private String sound;

	public SpeechKeyFrame() {
	}

	public SpeechKeyFrame(final float time) {
		super(time);
	}

	public SpeechKeyFrame(final float time, final String sound) {
		super(time);
		this.sound = sound;
	}

	@Override
	public void action(final Wanderer game, final CinematicController controller, final AbstractEntity target,
			final float time) {
		final String soundPath =
				"orig/book/" + Preferences.getPreferences().getOrElse("i18n.speech", "en") + "/" + this.sound;

		if (!WandererConstants.ASSET_MANAGER.isLoaded(soundPath, Sound3D.class)) {
			SpeechKeyFrame.LOG.warn("Hot-loading speech {}", soundPath);
			WandererConstants.ASSET_MANAGER.load(soundPath, Sound3D.class);
			WandererConstants.ASSET_MANAGER.finishLoadingAsset(soundPath);
		}

		final SoundEmitter3D emitter = WandererConstants.ASSET_MANAGER.get(soundPath, Sound3D.class).playGeneral();
		emitter.setDisposeOnStop(true);
		emitter.setPitch(controller.getRate());
		controller.addSound(emitter);
	}

	public String getSound() {
		return this.sound;
	}

	public void setSound(final String sound) {
		this.sound = sound;
	}
}
