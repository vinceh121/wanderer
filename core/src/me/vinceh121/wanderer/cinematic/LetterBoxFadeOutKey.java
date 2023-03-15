package me.vinceh121.wanderer.cinematic;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class LetterBoxFadeOutKey extends ActionKeyFrame {

	public LetterBoxFadeOutKey() {
		super();
	}

	public LetterBoxFadeOutKey(float time) {
		super(time);
	}

	@Override
	public void action(Wanderer game, CinematicController controller, AbstractEntity target, float time) {
		game.getLetterboxOverlay().stop();
	}
}
