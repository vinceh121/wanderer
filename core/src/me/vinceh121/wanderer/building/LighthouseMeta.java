package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.Wanderer;

public class LighthouseMeta extends AbstractBuildingMeta {
	public LighthouseMeta() {
	}

	public LighthouseMeta(int energyRequired, boolean red, String artifactModel, String artifactTexture) {
		super(energyRequired, red, artifactModel, artifactTexture);
	}

	@Override
	public AbstractBuilding createBuilding(Wanderer game) {
		return new Lighthouse(game, this);
	}
}
