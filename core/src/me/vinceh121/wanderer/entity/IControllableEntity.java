package me.vinceh121.wanderer.entity;

import me.vinceh121.wanderer.input.InputListener;

public interface IControllableEntity {
	InputListener getInputProcessor();

	void onTakeControl();

	void onRemoveControl();
}
