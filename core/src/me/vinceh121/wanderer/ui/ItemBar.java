package me.vinceh121.wanderer.ui;

import static me.vinceh121.wanderer.ui.UiUtils.drawFlip;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import me.vinceh121.wanderer.WandererConstants;

public class ItemBar extends Widget {
	private static final Texture TEX_SLOT = WandererConstants.ASSET_MANAGER
			.get("orig/if_emptyslot.n/slotslot_alpha.ktx", Texture.class);
	private static final Texture TEX_GAP = WandererConstants.ASSET_MANAGER.get("orig/if_emptyslot.n/gapgap_alpha.ktx",
			Texture.class);
	private static final Texture TEX_END = WandererConstants.ASSET_MANAGER.get("orig/if_emptyslot.n/endend_alpha.ktx",
			Texture.class);
	private int count;

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		if (this.count == 0)
			return;

		final int sX = (int) getX();
		for (int i = 0; i < this.count * 2; i += 2) {
			drawFlip(batch, TEX_SLOT, 32 * i + sX, getY());
			if (i == this.count * 2 - 2) {
				drawFlip(batch, TEX_END, 32 * (i + 1) + sX, getY());
			} else {
				drawFlip(batch, TEX_GAP, 32 * (i + 1) + sX, getY());
			}
		}
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
