package me.vinceh121.wanderer.entity.plane;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class MachineGunPlaneMeta extends AbstractPlaneMeta {

	@Override
	public AbstractEntity create(Wanderer game) {
		return new MachineGunPlane(game, this);
	}
}
