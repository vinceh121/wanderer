package me.vinceh121.wanderer.cinematic;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class SubtitleKeyFrame extends ActionKeyFrame {
	private String text;

	public SubtitleKeyFrame() {
	}

	public SubtitleKeyFrame(final float time) {
		super(time);
	}

	public SubtitleKeyFrame(final float time, final String text) {
		this(time);
		this.text = text;
	}

	@Override
	public void action(final Wanderer game, final CinematicController controller, final AbstractEntity target,
			final float time) {
		game.getSubtitle().setText(this.text);
	}

	public String getText() {
		return this.text;
	}

	public void setText(final String text) {
		this.text = text;
	}
}
