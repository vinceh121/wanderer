package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import me.vinceh121.wanderer.WandererConstants;

public class Subtitle extends Label {
	public Subtitle() {
		super("", WandererConstants.getDevSkin());
		this.setAlignment(Align.center);
		this.setWrap(true);
	}

	@Override
	public void draw(final Batch batch, final float parentAlpha) {
		final float margin = 5;

		for (final GlyphLayout layout : this.getBitmapFontCache().getLayouts()) {
			for (final GlyphRun run : layout.runs) {
				final float lineHeight = this.getBitmapFontCache().getFont().getLineHeight();
				// i don't understand why this is right
				final float y = this.getY(Align.bottomLeft) - run.y + lineHeight * 2 - margin;

				batch.draw(WandererConstants.BLACK_PIXEL,
						run.x - margin,
						y,
						run.width + margin * 2,
						lineHeight + margin * 2);
			}
		}

		super.draw(batch, parentAlpha);
	}

	@Override
	public void act(final float delta) {
		super.act(delta);

		final float height = 100;

		this.setWidth(Gdx.graphics.getWidth());
		this.setHeight(height);

		this.setPosition(0, 0, Align.bottomLeft);
	}
}
