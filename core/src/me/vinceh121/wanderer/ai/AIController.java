package me.vinceh121.wanderer.ai;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public abstract class AIController<T extends AbstractEntity> {
	protected final Wanderer game;
	protected final T target;

	public AIController(final Wanderer game, final T target) {
		this.game = game;
		this.target = target;
	}

	public T getTarget() {
		return this.target;
	}

	public abstract void tick(float delta);
}
