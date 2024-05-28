package me.vinceh121.wanderer.building;

import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class NavigationTowerPrototype extends AbstractBuildingPrototype {
	private Vector3 cameraOffset = new Vector3();

	public Vector3 getCameraOffset() {
		return cameraOffset;
	}

	public void setCameraOffset(Vector3 cameraOffset) {
		this.cameraOffset = cameraOffset;
	}

	@Override
	public AbstractEntity create(final Wanderer game) {
		return new NavigationTower(game, this);
	}
}
