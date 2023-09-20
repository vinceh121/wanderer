package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.ai.AIController;
import me.vinceh121.wanderer.entity.IControllableEntity;
import me.vinceh121.wanderer.input.InputListener;
import me.vinceh121.wanderer.input.InputListenerAdapter;

public abstract class AbstractControllableBuilding extends AbstractBuilding implements IControllableEntity {
	private boolean controlled;
	private InputListener inputListener;
	private AIController<?> aiController;

	public AbstractControllableBuilding(final Wanderer game, final AbstractBuildingMeta meta) {
		super(game, meta);
	}

	@Override
	public InputListener getInputProcessor() {
		if (this.inputListener == null) {
			this.inputListener = this.createInputProcessor();
		}
		return this.inputListener;
	}

	public InputListener createInputProcessor() {
		return new InputListenerAdapter(-1000);
	}

	@Override
	public void tick(float delta) {
		super.tick(delta);

		if (!this.controlled && this.aiController != null) {
			this.aiController.tick(delta);
		}
	}

	@Override
	public void onTakeControl() {
		this.controlled = true;
	}

	@Override
	public void onRemoveControl() {
		this.controlled = false;
	}

	public boolean isControlled() {
		return this.controlled;
	}

	public AIController<?> getAiController() {
		return aiController;
	}

	public void setAiController(AIController<?> aiController) {
		this.aiController = aiController;
	}
}
