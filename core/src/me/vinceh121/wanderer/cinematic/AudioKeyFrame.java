package me.vinceh121.wanderer.cinematic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.platform.audio.SoundEmitter3D;

public class AudioKeyFrame extends ActionKeyFrame {
	private static final Logger LOG = LogManager.getLogger(AudioKeyFrame.class);
	private String sound;

	public AudioKeyFrame() {
	}

	public AudioKeyFrame(final float time) {
		super(time);
	}

	public AudioKeyFrame(final float time, final String sound) {
		super(time);
		this.sound = sound;
	}

	@Override
	public void action(final Wanderer game, final CinematicController controller, final AbstractEntity target, final float time) {
		if (!WandererConstants.ASSET_MANAGER.isLoaded(this.sound, Sound3D.class)) {
			AudioKeyFrame.LOG.warn("Hot-loading sound {}", this.sound);
			WandererConstants.ASSET_MANAGER.load(this.sound, Sound3D.class);
			WandererConstants.ASSET_MANAGER.finishLoadingAsset(this.sound);
		}
		final SoundEmitter3D emitter = WandererConstants.ASSET_MANAGER.get(this.sound, Sound3D.class).playGeneral();
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
