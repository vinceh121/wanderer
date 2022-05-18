package me.vinceh121.wanderer.ui;

import static me.vinceh121.wanderer.ui.UiUtils.drawFlip;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import me.vinceh121.wanderer.WandererConstants;

public class EnergyBar extends Widget {
	private static final Texture TEX_BASE = WandererConstants.ASSET_MANAGER
			.get("orig/if_ebar.n/clan_energy_leftclan_energy_left_alpha.ktx", Texture.class);
	private static final Texture TEX_SEGMENT = WandererConstants.ASSET_MANAGER
			.get("orig/if_ebar.n/clan_energy_middleclan_energy_middle_alpha.ktx", Texture.class);
	private static final Texture TEX_TIP = WandererConstants.ASSET_MANAGER
			.get("orig/if_ebar.n/clan_energy_rightclan_energy_right_alpha.ktx", Texture.class);
	private static final Texture TEX_BAR = WandererConstants.ASSET_MANAGER.get("orig/if_ebar.n/texturenone.ktx",
			Texture.class);
	private int segmentCount = 5, value = 0, maxValue = 100;

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		final float maxHeight = /* base */116 + /* tip */2 + /* segments */32 * this.segmentCount;
		final float barHeight = this.value * maxHeight / maxValue;

		batch.setColor(0.3f, 0.5f, 0.8f, 1f);

		UiUtils.beginBlend(batch);
//		batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
		// width and height are swapped because of flip
		batch.draw(TEX_BAR, 103, 23, 0, 0, barHeight, 18, 1, 1, 90, 0, 0, TEX_BAR.getWidth(), TEX_BAR.getHeight(),
				false, true);
		UiUtils.endBlend(batch);
		batch.setColor(Color.WHITE);

		drawFlip(batch, TEX_BASE, getX(), getY());
		int i;
		for (i = 0; i < this.segmentCount; i++) {
			drawFlip(batch, TEX_SEGMENT, getX(), 32 * i + getY() + TEX_BASE.getHeight());
		}
		drawFlip(batch, TEX_TIP, getX(), 32 * i + getY() + TEX_BASE.getHeight());
	}

	public int getSegmentCount() {
		return segmentCount;
	}

	public void setSegmentCount(int segmentCount) {
		this.segmentCount = segmentCount;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public void updateSegments() {
		this.segmentCount = this.maxValue / 10;
	}
}
