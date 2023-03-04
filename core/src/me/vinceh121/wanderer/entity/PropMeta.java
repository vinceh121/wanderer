package me.vinceh121.wanderer.entity;

import me.vinceh121.wanderer.IMeta;
import me.vinceh121.wanderer.Wanderer;

public class PropMeta implements IMeta {
	private String displayModel, collideModel, texture, detailMapTexture;
	private float mass;

	public String getDisplayModel() {
		return displayModel;
	}

	public void setDisplayModel(String displayModel) {
		this.displayModel = displayModel;
	}

	public String getCollideModel() {
		return collideModel;
	}

	public void setCollideModel(String collideModel) {
		this.collideModel = collideModel;
	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public String getDetailMapTexture() {
		return detailMapTexture;
	}

	public void setDetailMapTexture(String detailMapTexture) {
		this.detailMapTexture = detailMapTexture;
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	@Override
	public AbstractEntity create(Wanderer game) {
		return new Prop(game, this);
	}
}
