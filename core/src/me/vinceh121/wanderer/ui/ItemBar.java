package me.vinceh121.wanderer.ui;

import static me.vinceh121.wanderer.ui.UiUtils.drawFlip;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.artifact.ArtifactMeta;
import me.vinceh121.wanderer.entity.DisplayModel;

public class ItemBar extends WandererWidget {
	private static final Texture TEX_SLOT = WandererConstants.ASSET_MANAGER
			.get("orig/if_emptyslot.n/slotslot_alpha.ktx", Texture.class);
	private static final Texture TEX_GAP = WandererConstants.ASSET_MANAGER.get("orig/if_emptyslot.n/gapgap_alpha.ktx",
			Texture.class);
	private static final Texture TEX_END = WandererConstants.ASSET_MANAGER.get("orig/if_emptyslot.n/endend_alpha.ktx",
			Texture.class);
	private final Quaternion rotation = new Quaternion();
	private float angle;

	private Array<ArtifactMeta> belt = new Array<>();
	private int count;

	public ItemBar(Wanderer game) {
		super(game);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		if (this.count == 0)
			return;

		this.angle += 3f;
		this.rotation.setFromAxis(Vector3.Y, this.angle);

		final int sX = (int) getX();
		for (int i = 0; i < this.count * 2; i += 2) {
			drawFlip(batch, TEX_SLOT, 32 * i + sX, getY());

			if (i / 2 < this.belt.size) {
				ArtifactMeta artifact = this.belt.get(i / 2);

				DisplayModel m = new DisplayModel(artifact.getArtifactModel(), artifact.getArtifactTexture());
				m.setAbsoluteTransform(new Matrix4(new Vector3(32 * i + sX + TEX_SLOT.getWidth() / 2, getY() + 15, -10),
						this.rotation, new Vector3(1, 1, 1)));

				m.addTextureAttribute(ColorAttribute.createEmissive(artifact.getArtifactColor()));
				m.render(this.game.getGraphicsManager().getModelBatch(), this.game.getGraphicsManager().getEnv());
			}

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

	public Array<ArtifactMeta> getBelt() {
		return belt;
	}

	public void setBelt(Array<ArtifactMeta> belt) {
		this.belt = belt;
	}
}
