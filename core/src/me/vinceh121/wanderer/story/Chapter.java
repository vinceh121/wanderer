package me.vinceh121.wanderer.story;

import java.util.List;

public class Chapter {
	private List<Part> parts;

	public Chapter() {
	}

	public Chapter(List<Part> parts) {
		this.parts = parts;
	}

	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> parts) {
		this.parts = parts;
	}

	@Override
	public String toString() {
		return "Chapter [parts=" + parts + "]";
	}
}
