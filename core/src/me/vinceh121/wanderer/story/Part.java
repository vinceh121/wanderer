package me.vinceh121.wanderer.story;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.vinceh121.wanderer.event.Event;
import me.vinceh121.wanderer.event.EventDispatcher;

public class Part extends EventDispatcher {
	private final Map<String, Object> state = new Hashtable<>();
	private List<String> objectives;
	private String title;
	private Runnable partStart;

	public Part() {
		this.state.put("objectivesCompleted", new HashSet<>());
	}

	public Part(final String title, final List<String> objectives) {
		this();
		this.title = title;
		this.objectives = objectives;
	}

	@SuppressWarnings("unchecked")
	public Set<Integer> getObjectivesCompleted() {
		return (Set<Integer>) this.state.get("objectivesCompleted");
	}

	public void addObjectiveCompleted(final int objectiveCompleted) {
		assert objectiveCompleted < this.objectives.size() : "objectiveCompleted " + objectiveCompleted
				+ " out of bounds for " + this.objectives.size() + " objectives";
		this.getObjectivesCompleted().add(objectiveCompleted);
		this.dispatchEvent(new ObjectiveCompletedEvent(objectiveCompleted));
	}

	public Map<String, Object> getState() {
		return this.state;
	}

	public List<String> getObjectives() {
		return this.objectives;
	}

	public void setObjectives(final List<String> objectives) {
		this.objectives = objectives;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public Runnable getPartStart() {
		return this.partStart;
	}

	public void setPartStart(final Runnable partStart) {
		this.partStart = partStart;
	}

	@Override
	public String toString() {
		return "Part [objectives=" + this.objectives + ", title=" + this.title + "]";
	}

	public static class ObjectiveCompletedEvent extends Event {
		private final int objectiveCompleted;

		public ObjectiveCompletedEvent(final int objectiveCompleted) {
			super("objectiveCompleted");
			this.objectiveCompleted = objectiveCompleted;
		}

		public int getObjectiveCompleted() {
			return this.objectiveCompleted;
		}
	}
}
