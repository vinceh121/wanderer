package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class LighthousePrototype extends AbstractBuildingPrototype {
	public LighthousePrototype() {
		this.setSlotType(SlotType.LIGHTHOUSE);
	}

	public LighthousePrototype(final int energyRequired, final boolean red, final String artifactModel,
			final String artifactTexture) {
		super(energyRequired, red, artifactModel, artifactTexture);
		this.setSlotType(SlotType.LIGHTHOUSE);
	}

	@Override
	public AbstractEntity create(final Wanderer game) {
		return new Lighthouse(game, this);
	}
}
