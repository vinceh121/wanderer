package me.vinceh121.wanderer.entity.guntower;

import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.building.AbstractBuildingMeta;

public abstract class AbstractGuntowerMeta extends AbstractBuildingMeta {
	private Vector3 cameraOffset = new Vector3();
	private float polarMin = 0.1f, polarMax = 0.9f;
	private String fireSound;
	private boolean hasAi;

	public Vector3 getCameraOffset() {
		return this.cameraOffset;
	}

	public void setCameraOffset(final Vector3 cameraOffset) {
		this.cameraOffset = cameraOffset;
	}

	public float getPolarMin() {
		return this.polarMin;
	}

	public void setPolarMin(final float polarMin) {
		this.polarMin = polarMin;
	}

	public float getPolarMax() {
		return this.polarMax;
	}

	public void setPolarMax(final float polarMax) {
		this.polarMax = polarMax;
	}

	public String getFireSound() {
		return this.fireSound;
	}

	public void setFireSound(final String fireSound) {
		this.fireSound = fireSound;
	}

	public boolean isHasAi() {
		return this.hasAi;
	}

	public void setHasAi(boolean hasAi) {
		this.hasAi = hasAi;
	}
}
