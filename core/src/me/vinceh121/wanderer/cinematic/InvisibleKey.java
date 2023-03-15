package me.vinceh121.wanderer.cinematic;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class InvisibleKey extends ActionKeyFrame {
	public InvisibleKey() {
		super();
	}

	public InvisibleKey(float time) {
		super(time);
	}

	@Override
	public void action(Wanderer game, CinematicController controller, AbstractEntity target, float time) {
		target.setInvisible(true);
	}
}
