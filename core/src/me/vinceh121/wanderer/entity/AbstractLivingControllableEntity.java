package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

import me.vinceh121.wanderer.Wanderer;

public abstract class AbstractLivingControllableEntity extends AbstractLivingEntity implements IControllableEntity {
	private boolean isControlled;

	public AbstractLivingControllableEntity(Wanderer game) {
		super(game);
	}

	@Override
	public InputProcessor getInputProcessor() {
		return new InputAdapter();
	}

	@Override
	public void onTakeControl() {
		this.isControlled = true;
	}

	@Override
	public void onRemoveControl() {
		this.isControlled = false;
	}

	public boolean isControlled() {
		return isControlled;
	}
}
