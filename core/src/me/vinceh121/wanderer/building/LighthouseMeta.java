package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.Wanderer;

public class LighthouseMeta extends AbstractBuildingMeta {
	public LighthouseMeta() {
	}

	public LighthouseMeta(final int energyRequired, final boolean red, final String artifactModel,
			final String artifactTexture) {
		super(energyRequired, red, artifactModel, artifactTexture);
	}

	@Override
	public AbstractBuilding createBuilding(final Wanderer game) {
		return new Lighthouse(game, this);
	}
}
