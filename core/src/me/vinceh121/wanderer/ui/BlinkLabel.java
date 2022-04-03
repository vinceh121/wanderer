package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BlinkLabel extends Label {
	private long attackTime = 50, sustainTime = 2000, releaseTime = 100, blinkStart;
	/**
	 * -1 not blinking, 0 attacking, 1 sustaining, 2 releasing
	 *
	 * wash, rince, repeat
	 */
	private int state;

	public BlinkLabel(CharSequence text, LabelStyle style) {
		super(text, style);
	}

	public BlinkLabel(CharSequence text, Skin skin, String fontName, Color color) {
		super(text, skin, fontName, color);
	}

	public BlinkLabel(CharSequence text, Skin skin, String fontName, String colorName) {
		super(text, skin, fontName, colorName);
	}

	public BlinkLabel(CharSequence text, Skin skin, String styleName) {
		super(text, skin, styleName);
	}

	public BlinkLabel(CharSequence text, Skin skin) {
		super(text, skin);
	}

	public void blink() {
		this.state = 0;
		this.blinkStart = System.currentTimeMillis();
	}

	@Override
	public void act(float delta) {
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
			this.setAlpha(1 - (elapsed / (this.releaseTime)));
			if (elapsed > this.releaseTime) {
				this.state++;
			}
		}

		super.act(delta);
	}

	public void setAlpha(float a) {
		this.getColor().a = a;
	}

	public float getAlpha() {
		return this.getColor().a;
	}
}
