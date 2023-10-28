package me.vinceh121.wanderer.entity.plane;

import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.IMeta;
import me.vinceh121.wanderer.entity.DisplayModel;

public abstract class AbstractPlaneMeta implements IMeta {
	private Array<DisplayModel> displayModels = new Array<>();
	private Array<DisplayModel> explosionParts = new Array<>();
	private String collisionModel;
	private final PlaneSpeedProfile normal = new PlaneSpeedProfile(), turbo = new PlaneSpeedProfile();
	private float maxTurboTime;

	public Array<DisplayModel> getDisplayModels() {
		return displayModels;
	}

	public void setDisplayModels(Array<DisplayModel> displayModels) {
		this.displayModels = displayModels;
	}

	public Array<DisplayModel> getExplosionParts() {
		return explosionParts;
	}

	public void setExplosionParts(Array<DisplayModel> explosionParts) {
		this.explosionParts = explosionParts;
	}

	public String getCollisionModel() {
		return collisionModel;
	}

	public void setCollisionModel(String collisionModel) {
		this.collisionModel = collisionModel;
	}

	public PlaneSpeedProfile getNormal() {
		return this.normal;
	}

	public PlaneSpeedProfile getTurbo() {
		return this.turbo;
	}

	public float getMaxTurboTime() {
		return maxTurboTime;
	}

	public void setMaxTurboTime(float maxTurboTime) {
		this.maxTurboTime = maxTurboTime;
	}
}
