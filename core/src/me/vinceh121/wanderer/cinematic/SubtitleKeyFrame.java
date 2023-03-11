package me.vinceh121.wanderer.cinematic;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class SubtitleKeyFrame extends ActionKeyFrame {
	private String text;

	public SubtitleKeyFrame() {
		super();
	}

	public SubtitleKeyFrame(float time) {
		super(time);
	}

	public SubtitleKeyFrame(float time, String text) {
		this(time);
		this.text = text;
	}

	@Override
	public void action(Wanderer game, AbstractEntity target, float time) {
		game.getSubtitle().setText(this.text);
		System.out.println(this.text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
