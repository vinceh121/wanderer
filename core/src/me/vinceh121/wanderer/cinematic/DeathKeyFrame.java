package me.vinceh121.wanderer.cinematic;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.ILivingEntity;

public class DeathKeyFrame extends ActionKeyFrame {
	@Override
	public void action(final Wanderer game, final CinematicController controller, final AbstractEntity target,
			final float time) {
		if (target instanceof ILivingEntity) {
			((ILivingEntity) target).onDeath();
		} else {
			throw new IllegalArgumentException("Trying to apply DeathKeyFrame to non-living entity " + target);
		}
	}
}
