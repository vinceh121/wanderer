package me.vinceh121.wanderer.entity;

import me.vinceh121.wanderer.IMeta;
import me.vinceh121.wanderer.Wanderer;

public class PropMeta implements IMeta {
	private String displayModel, collideModel, texture, detailMapTexture;
	private float mass;

	public String getDisplayModel() {
		return this.displayModel;
	}

	public void setDisplayModel(final String displayModel) {
		this.displayModel = displayModel;
	}

	public String getCollideModel() {
		return this.collideModel;
	}

	public void setCollideModel(final String collideModel) {
		this.collideModel = collideModel;
	}

	public String getTexture() {
		return this.texture;
	}

	public void setTexture(final String texture) {
		this.texture = texture;
	}

	public String getDetailMapTexture() {
		return this.detailMapTexture;
	}

	public void setDetailMapTexture(final String detailMapTexture) {
		this.detailMapTexture = detailMapTexture;
	}

	public float getMass() {
		return this.mass;
	}

	public void setMass(final float mass) {
		this.mass = mass;
	}

	@Override
	public AbstractEntity create(final Wanderer game) {
		return new Prop(game, this);
	}
}
