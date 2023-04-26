package me.vinceh121.wanderer.guntower;

import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.building.AbstractBuildingMeta;

public abstract class AbstractGuntowerMeta extends AbstractBuildingMeta {
	private Vector3 cameraOffset = new Vector3();
	private float polarMin = 0.1f, polarMax = 0.9f;

	public Vector3 getCameraOffset() {
		return cameraOffset;
	}

	public void setCameraOffset(Vector3 cameraOffset) {
		this.cameraOffset = cameraOffset;
	}

	public float getPolarMin() {
		return polarMin;
	}

	public void setPolarMin(float polarMin) {
		this.polarMin = polarMin;
	}

	public float getPolarMax() {
		return polarMax;
	}

	public void setPolarMax(float polarMax) {
		this.polarMax = polarMax;
	}
}
