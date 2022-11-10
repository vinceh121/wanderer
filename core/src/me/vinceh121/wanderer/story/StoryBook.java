package me.vinceh121.wanderer.story;

import java.util.List;

public class StoryBook {
	private List<Chapter> chapters;

	public StoryBook() {
	}

	public StoryBook(final List<Chapter> chapters) {
		this.chapters = chapters;
	}

	public List<Chapter> getChapters() {
		return this.chapters;
	}

	public void setChapters(final List<Chapter> chapters) {
		this.chapters = chapters;
	}

	@Override
	public String toString() {
		return "StoryBook [chapters=" + this.chapters + "]";
	}
}
