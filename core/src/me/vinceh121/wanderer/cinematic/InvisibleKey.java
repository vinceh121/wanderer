package me.vinceh121.wanderer.cinematic;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class InvisibleKey extends ActionKeyFrame {
	public InvisibleKey() {
	}

	public InvisibleKey(final float time) {
		super(time);
	}

	@Override
	public void action(final Wanderer game, final CinematicController controller, final AbstractEntity target,
			final float time) {
		target.setInvisible(true);
	}
}
