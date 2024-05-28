package me.vinceh121.wanderer.ai;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class TaskAIController<T extends AbstractEntity> extends AIController<T> {
	private Task<T> currentTask;

	public TaskAIController(Wanderer game, T target) {
		super(game, target);
	}

	@Override
	public void tick(float delta) {
		if (this.currentTask != null) {
			final Task<T> maybeNewTask = this.currentTask.process(delta, this.game, this.target);
			
			if (maybeNewTask != null) {
				this.currentTask = maybeNewTask;
			}
		}
	}

	public Task<T> getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(Task<T> currentTask) {
		this.currentTask = currentTask;
	}
}
