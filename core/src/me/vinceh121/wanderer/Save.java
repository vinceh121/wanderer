package me.vinceh121.wanderer;

import java.util.Map;

public class Save {
	private String storyBook;
	private ID controlled, playerClan;
	private int chapter = -1, part = -1;
	private float time;
	private Map<String, Object> partState;
	private MapW map;

	public String getStoryBook() {
		return this.storyBook;
	}

	public void setStoryBook(final String storyBook) {
		this.storyBook = storyBook;
	}

	public ID getControlled() {
		return this.controlled;
	}

	public void setControlled(final ID controlled) {
		this.controlled = controlled;
	}

	public ID getPlayerClan() {
		return this.playerClan;
	}

	public void setPlayerClan(final ID playerClan) {
		this.playerClan = playerClan;
	}

	public int getChapter() {
		return this.chapter;
	}

	public void setChapter(final int chapter) {
		this.chapter = chapter;
	}

	public int getPart() {
		return this.part;
	}

	public void setPart(final int part) {
		this.part = part;
	}

	public float getTime() {
		return this.time;
	}

	public void setTime(final float time) {
		this.time = time;
	}

	public Map<String, Object> getPartState() {
		return this.partState;
	}

	public void setPartState(final Map<String, Object> partState) {
		this.partState = partState;
	}

	public MapW getMap() {
		return this.map;
	}

	public void setMap(final MapW map) {
		this.map = map;
	}
}
