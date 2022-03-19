package me.vinceh121.wanderer.entity;

import me.vinceh121.wanderer.Wanderer;

/**
 * A prop is the simplest entity. Just a collidable object in the world.
 */
public class Prop extends AbstractEntity {
	public Prop(final Wanderer game) {
		super(game);
	}

	public Prop(final Wanderer game, final String displayModel, final String collideModel, final String texture,
			final float mass) {
		this(game);
		this.setDisplayModel(displayModel);
		this.setCollideModel(collideModel);
		this.setDisplayTexture(texture);
		this.setMass(mass);
	}
}
