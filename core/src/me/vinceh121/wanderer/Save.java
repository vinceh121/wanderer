package me.vinceh121.wanderer;

import java.util.Map;

public class Save {
	private String storyBook;
	private ID controlled, playerClan;
	private int chapter = -1, part = -1;
	private Map<String, Object> partState;
	private MapW map;

	public String getStoryBook() {
		return storyBook;
	}

	public void setStoryBook(String storyBook) {
		this.storyBook = storyBook;
	}

	public ID getControlled() {
		return controlled;
	}

	public void setControlled(ID controlled) {
		this.controlled = controlled;
	}

	public ID getPlayerClan() {
		return playerClan;
	}

	public void setPlayerClan(ID playerClan) {
		this.playerClan = playerClan;
	}

	public int getChapter() {
		return chapter;
	}

	public void setChapter(int chapter) {
		this.chapter = chapter;
	}

	public int getPart() {
		return part;
	}

	public void setPart(int part) {
		this.part = part;
	}

	public Map<String, Object> getPartState() {
		return partState;
	}

	public void setPartState(Map<String, Object> partState) {
		this.partState = partState;
	}

	public MapW getMap() {
		return map;
	}

	public void setMap(MapW map) {
		this.map = map;
	}
}
