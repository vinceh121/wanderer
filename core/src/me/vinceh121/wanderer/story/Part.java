package me.vinceh121.wanderer.story;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Part {
	private final Map<String, Object> state = new Hashtable<>();
	private List<String> objectives;
	private String title;
	private Runnable partStart;

	public Part() {
	}

	public Part(final String title, final List<String> objectives) {
		this.title = title;
		this.objectives = objectives;
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
}
