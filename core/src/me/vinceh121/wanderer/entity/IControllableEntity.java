package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.physics.bullet.dynamics.btActionInterface;

public interface IControllableEntity {
	InputProcessor getInputProcessor();

	btActionInterface getBulletAction();

	void onTakeControl();

	void onRemoveControl();
}
