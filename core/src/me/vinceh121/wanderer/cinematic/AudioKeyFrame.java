package me.vinceh121.wanderer.cinematic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.platform.audio.Sound3D;

public class AudioKeyFrame extends ActionKeyFrame {
	private static final Logger LOG = LogManager.getLogger(AudioKeyFrame.class);
	private String sound;

	public AudioKeyFrame() {
		super();
	}

	public AudioKeyFrame(float time) {
		super(time);
	}

	public AudioKeyFrame(float time, String sound) {
		super(time);
		this.sound = sound;
	}

	@Override
	public void action(Wanderer game, AbstractEntity target, float time) {
		if (!WandererConstants.ASSET_MANAGER.isLoaded(this.sound, Sound3D.class)) {
			LOG.warn("Hot-loading sound {}", this.sound);
			WandererConstants.ASSET_MANAGER.load(this.sound, Sound3D.class);
			WandererConstants.ASSET_MANAGER.finishLoadingAsset(this.sound);
		}
		WandererConstants.ASSET_MANAGER.get(this.sound, Sound3D.class).playGeneral();
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}
}
