package me.vinceh121.wanderer.entity;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.input.InputListener;
import me.vinceh121.wanderer.input.InputListenerAdapter;

public abstract class AbstractLivingControllableEntity extends AbstractLivingEntity implements IControllableEntity {
	private boolean isControlled;

	public AbstractLivingControllableEntity(final Wanderer game) {
		super(game);
	}

	@Override
	public InputListener getInputProcessor() {
		return new InputListenerAdapter(-1000);
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
		return this.isControlled;
	}
}
