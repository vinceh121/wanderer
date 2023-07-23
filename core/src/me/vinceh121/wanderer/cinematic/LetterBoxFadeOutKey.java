package me.vinceh121.wanderer.cinematic;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class LetterBoxFadeOutKey extends ActionKeyFrame {

	public LetterBoxFadeOutKey() {
	}

	public LetterBoxFadeOutKey(final float time) {
		super(time);
	}

	@Override
	public void action(final Wanderer game, final CinematicController controller, final AbstractEntity target, final float time) {
		game.getLetterboxOverlay().stop();
	}
}
