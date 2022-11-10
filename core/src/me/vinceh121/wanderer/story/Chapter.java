package me.vinceh121.wanderer.story;

import java.util.List;

public class Chapter {
	private List<Part> parts;

	public Chapter() {
	}

	public Chapter(final List<Part> parts) {
		this.parts = parts;
	}

	public List<Part> getParts() {
		return this.parts;
	}

	public void setParts(final List<Part> parts) {
		this.parts = parts;
	}

	@Override
	public String toString() {
		return "Chapter [parts=" + this.parts + "]";
	}
}
