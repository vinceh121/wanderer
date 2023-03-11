package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import me.vinceh121.wanderer.WandererConstants;

public class LetterboxOverlay extends Widget {
	private final FloatAction fadeIn = new FloatAction(0, 1, 2, Interpolation.smooth);
	private final FloatAction fadeOut = new FloatAction(1, 0, 2, Interpolation.smooth);
	private float progress;
	private boolean started;

	public LetterboxOverlay() {
		this.fadeOut.finish();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		// standard cinema screen is 12/5, and width/(height * x) = 12/5 <-> 0.416667 width / height
		float barsHeight = (Gdx.graphics.getHeight() - (0.416667f
				* ((float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight()) * Gdx.graphics.getHeight())) / 2;

		barsHeight *= this.progress;

		batch.draw(WandererConstants.BLACK_PIXEL, 0, 0, Gdx.graphics.getWidth(), barsHeight);
		batch.draw(WandererConstants.BLACK_PIXEL, 0, Gdx.graphics.getHeight() - barsHeight, Gdx.graphics.getWidth(), barsHeight);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (started) {
			this.fadeIn.act(delta);
			this.progress = this.fadeIn.getValue();
		} else {
			this.fadeOut.act(delta);
			this.progress = this.fadeOut.getValue();
		}
		this.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void start() {
		this.started = true;
		this.fadeIn.restart();
	}

	public void stop() {
		this.started = false;
		this.fadeOut.restart();
	}

	public boolean isStarted() {
		return this.started;
	}
}
