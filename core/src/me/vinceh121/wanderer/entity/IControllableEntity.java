package me.vinceh121.wanderer.entity;

import me.vinceh121.wanderer.input.InputListener;

public interface IControllableEntity {
	/**
	 * The returned InputListener must be the same for each entity.
	 */
	InputListener getInputProcessor();

	void onTakeControl();

	void onRemoveControl();
}
