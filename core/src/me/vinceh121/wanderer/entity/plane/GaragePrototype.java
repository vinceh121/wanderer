package me.vinceh121.wanderer.entity.plane;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.building.AbstractBuildingPrototype;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class GaragePrototype extends AbstractBuildingPrototype {
	@Override
	public AbstractEntity create(Wanderer game) {
		return new Garage(game, this);
	}
}
