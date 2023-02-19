package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Logger;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.MetaRegistry;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.glx.TiledMaterialAttribute;

/**
 * A prop is the simplest entity. Just a collidable object in the world.
 */
public class Prop extends AbstractEntity {
	private static final Logger LOG = new Logger(Prop.class.getCanonicalName());
	private final PropMeta meta;

	public Prop(final Wanderer game, PropMeta meta) {
		super(game);
		this.meta = meta;
		DisplayModel mdl = new DisplayModel(meta.getDisplayModel(), meta.getTexture());

		this.addModel(mdl);
		this.setCollideModel(meta.getCollideModel());
		this.setMass(meta.getMass());

		if (meta.getDetailMapTexture() != null) {
			if (!WandererConstants.ASSET_MANAGER.isLoaded(meta.getDetailMapTexture(), Texture.class)) {
				LOG.error("Hot loading tiled texture " + meta.getDetailMapTexture());
				WandererConstants.ASSET_MANAGER
					.load(meta.getDetailMapTexture(), Texture.class, WandererConstants.MIPMAPS);
				WandererConstants.ASSET_MANAGER.finishLoadingAsset(meta.getDetailMapTexture());
			}
			mdl.addTextureAttribute(TiledMaterialAttribute
				.create(WandererConstants.ASSET_MANAGER.get(meta.getDetailMapTexture(), Texture.class)));
		}
	}

	public Prop(final Wanderer game, final String displayModel, final String collideModel, final String texture,
			final float mass) {
		this(game, null);
		this.addModel(new DisplayModel(displayModel, texture));
		this.setCollideModel(collideModel);
		this.setMass(mass);
	}

	@Override
	public void writeState(final ObjectNode node) {
		super.writeState(node);
		if (meta != null) {
			node.put("meta", MetaRegistry.getInstance().getReverse(this.meta));
		} else {
			node.putNull("meta");
		}
	}
}
