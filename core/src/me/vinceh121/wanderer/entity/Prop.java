package me.vinceh121.wanderer.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.graphics.Texture;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.PrototypeRegistry;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.glx.TiledMaterialAttribute;

/**
 * A prop is the simplest entity. Just a collidable object in the world.
 */
public class Prop extends AbstractEntity {
	private static final Logger LOG = LogManager.getLogger(Prop.class);
	private final PropPrototype prototype;

	public Prop(final Wanderer game, final PropPrototype prototype) {
		super(game);
		this.prototype = prototype;
		final DisplayModel mdl = new DisplayModel(prototype.getDisplayModel(), prototype.getTexture());

		this.addModel(mdl);
		this.setCollideModel(prototype.getCollideModel());
		this.setMass(prototype.getMass());

		if (prototype.getDetailMapTexture() != null) {
			if (!WandererConstants.ASSET_MANAGER.isLoaded(prototype.getDetailMapTexture(), Texture.class)) {
				Prop.LOG.error("Hot loading tiled texture {}", prototype.getDetailMapTexture());
				WandererConstants.ASSET_MANAGER
					.load(prototype.getDetailMapTexture(), Texture.class, WandererConstants.MIPMAPS);
				WandererConstants.ASSET_MANAGER.finishLoadingAsset(prototype.getDetailMapTexture());
			}
			mdl.addTextureAttribute(TiledMaterialAttribute
				.create(WandererConstants.ASSET_MANAGER.get(prototype.getDetailMapTexture(), Texture.class)));
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
		if (this.prototype != null) {
			node.put("prototype", PrototypeRegistry.getInstance().getReverse(this.prototype));
		} else {
			node.putNull("prototype");
		}
	}
}
