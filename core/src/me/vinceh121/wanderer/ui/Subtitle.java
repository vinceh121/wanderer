package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import me.vinceh121.wanderer.WandererConstants;

public class Subtitle extends Label {
	public Subtitle() {
		super("", WandererConstants.getDevSkin());
		this.setAlignment(Align.center);
		this.getStyle().background = new TextureRegionDrawable(WandererConstants.BLACK_PIXEL);
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		final float height = 100;

		this.setWidth(Gdx.graphics.getWidth());
		this.setHeight(height);

		this.setPosition(0, 0, Align.bottomLeft);
	}
}
