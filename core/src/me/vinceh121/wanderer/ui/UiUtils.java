package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public final class UiUtils {
	public static void drawFlip(Batch batch, Texture tex, float x, float y) {
		batch.draw(tex, x, y, tex.getWidth(), tex.getHeight(), 0, 0, tex.getWidth(), tex.getHeight(), false, true);
	}
}
