package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BlinkLabel extends Label {
	private final long attackTime = 50, sustainTime = 2000, releaseTime = 100;
	private long blinkStart;
	/**
	 * -1 not blinking, 0 attacking, 1 sustaining, 2 releasing
	 *
	 * wash, rince, repeat
	 */
	private int state;

	public BlinkLabel(final CharSequence text, final LabelStyle style) {
		super(text, style);
	}

	public BlinkLabel(final CharSequence text, final Skin skin, final String fontName, final Color color) {
		super(text, skin, fontName, color);
	}

	public BlinkLabel(final CharSequence text, final Skin skin, final String fontName, final String colorName) {
		super(text, skin, fontName, colorName);
	}

	public BlinkLabel(final CharSequence text, final Skin skin, final String styleName) {
		super(text, skin, styleName);
	}

	public BlinkLabel(final CharSequence text, final Skin skin) {
		super(text, skin);
	}

	public void blink() {
		this.state = 0;
		this.blinkStart = System.currentTimeMillis();
	}

	@Override
	public void act(final float delta) {
		final float elapsed = System.currentTimeMillis() - this.blinkStart;
		if (this.state == 0) {
			this.setAlpha(elapsed / this.attackTime);
			if (elapsed > this.attackTime) {
				this.state++;
			}
		} else if (this.state == 1) {
			if (elapsed > this.attackTime + this.sustainTime) {
				this.blinkStart = System.currentTimeMillis();
				this.state++;
			}
		} else if (this.state == 2) {
			this.setAlpha(1 - elapsed / this.releaseTime);
			if (elapsed > this.releaseTime) {
				this.state++;
			}
		}

		super.act(delta);
	}

	public void setAlpha(final float a) {
		this.getColor().a = a;
	}

	public float getAlpha() {
		return this.getColor().a;
	}
}
