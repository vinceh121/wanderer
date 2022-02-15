package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.InputProcessor;

public interface IControllableEntity {
	InputProcessor getInputProcessor();

	void onTakeControl();

	void onRemoveControl();
}
