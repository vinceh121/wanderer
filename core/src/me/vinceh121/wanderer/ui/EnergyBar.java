package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.clan.Clan;

public class EnergyBar extends Widget {
	private static final Texture TEX_BASE = WandererConstants.ASSET_MANAGER
		.get("orig/if_ebar.n/clan_energy_leftclan_energy_left_alpha.ktx", Texture.class);
	private static final Texture TEX_SEGMENT = WandererConstants.ASSET_MANAGER
		.get("orig/if_ebar.n/clan_energy_middleclan_energy_middle_alpha.ktx", Texture.class);
	private static final Texture TEX_TIP = WandererConstants.ASSET_MANAGER
		.get("orig/if_ebar.n/clan_energy_rightclan_energy_right_alpha.ktx", Texture.class);
	private static final Texture TEX_BAR =
			WandererConstants.ASSET_MANAGER.get("orig/if_ebar.n/texturenone.ktx", Texture.class);
	private int segmentCount = 5, value = 0, maxValue = 100;
	private Clan clan;

	@Override
	public void draw(final Batch batch, final float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (this.clan != null) {
			this.setMaxValue(this.clan.getMaxEnergy());
			this.updateSegments();
			this.setValue(this.clan.getEnergy());
		}

		final float maxHeight = /* base */116 + /* tip */2 + /* segments */32 * this.segmentCount;
		final float barHeight = this.value * maxHeight / this.maxValue;

		batch.setColor(0.3f, 0.5f, 0.8f, 1f);

		UiUtils.beginBlend(batch);
//		batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
		// width and height are swapped because of flip
		batch.draw(EnergyBar.TEX_BAR,
				103,
				23,
				0,
				0,
				barHeight,
				18,
				1,
				1,
				90,
				0,
				0,
				EnergyBar.TEX_BAR.getWidth(),
				EnergyBar.TEX_BAR.getHeight(),
				false,
				true);
		UiUtils.endBlend(batch);
		batch.setColor(Color.WHITE);

		UiUtils.drawFlip(batch, EnergyBar.TEX_BASE, this.getX(), this.getY());
		int i;
		for (i = 0; i < this.segmentCount; i++) {
			UiUtils.drawFlip(batch,
					EnergyBar.TEX_SEGMENT,
					this.getX(),
					32 * i + this.getY() + EnergyBar.TEX_BASE.getHeight());
		}
		UiUtils.drawFlip(batch, EnergyBar.TEX_TIP, this.getX(), 32 * i + this.getY() + EnergyBar.TEX_BASE.getHeight());
	}

	public int getSegmentCount() {
		return this.segmentCount;
	}

	public void setSegmentCount(final int segmentCount) {
		this.segmentCount = segmentCount;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(final int value) {
		this.value = value;
	}

	public int getMaxValue() {
		return this.maxValue;
	}

	public void setMaxValue(final int maxValue) {
		this.maxValue = maxValue;
	}

	public Clan getClan() {
		return this.clan;
	}

	public void setClan(final Clan clan) {
		this.clan = clan;
	}

	public void updateSegments() {
		this.segmentCount = this.maxValue / 10;
	}
}
