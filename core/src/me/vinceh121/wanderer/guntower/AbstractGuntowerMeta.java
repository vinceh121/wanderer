package me.vinceh121.wanderer.guntower;

import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.building.AbstractBuildingMeta;

public abstract class AbstractGuntowerMeta extends AbstractBuildingMeta {
	private Vector3 cameraOffset = new Vector3();

	public Vector3 getCameraOffset() {
		return cameraOffset;
	}

	public void setCameraOffset(Vector3 cameraOffset) {
		this.cameraOffset = cameraOffset;
	}
}
