package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class LighthouseMeta extends AbstractBuildingMeta {
	public LighthouseMeta() {
		this.setSlotType(SlotType.LIGHTHOUSE);
	}

	public LighthouseMeta(final int energyRequired, final boolean red, final String artifactModel,
			final String artifactTexture) {
		super(energyRequired, red, artifactModel, artifactTexture);
		this.setSlotType(SlotType.LIGHTHOUSE);
	}

	@Override
	public AbstractEntity create(final Wanderer game) {
		return new Lighthouse(game, this);
	}
}
