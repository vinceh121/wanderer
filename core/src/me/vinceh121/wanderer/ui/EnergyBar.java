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
	private int segmentCount = 5, value = 50, maxValue = 100;

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.setColor(0.3f, 0.5f, 0.8f, 0.5f);
		batch.setColor(parentAlpha, parentAlpha, parentAlpha, parentAlpha);
		batch.draw(TEX_BAR, 104, 20, 0, 0, 200, 18, 1, 1, 90, 0, 0, TEX_BAR.getWidth(), TEX_BAR.getHeight(), false,
				false);
		batch.setColor(Color.WHITE);

		drawFlip(batch, TEX_BASE, getX(), getY());
		int i;
		for (i = 0; i < this.segmentCount; i++) {
			drawFlip(batch, TEX_SEGMENT, getX(), 32 * i + getY() + TEX_BASE.getHeight());
		}
		drawFlip(batch, TEX_TIP, getX(), 32 * i + getY() + TEX_BASE.getHeight());
	}
}
