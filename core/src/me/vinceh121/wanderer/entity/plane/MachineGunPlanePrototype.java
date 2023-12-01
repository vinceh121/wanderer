package me.vinceh121.wanderer.entity.plane;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class MachineGunPlanePrototype extends AbstractPlanePrototype {

	@Override
	public AbstractEntity create(final Wanderer game) {
		return new MachineGunPlane(game, this);
	}
}
