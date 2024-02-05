package me.vinceh121.wanderer.ai;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.IControllableEntity;

public abstract class Task<T extends IControllableEntity> {
	/**
	 * @param delta  Delta time in seconds
	 * @param game TODO
	 * @param controlled Entity being controlled
	 * @return The next task to use, or null to keep current task
	 */
	public abstract Task<T> process(final float delta, final Wanderer game, final T controlled);
}
