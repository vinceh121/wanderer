package me.vinceh121.wanderer.guntower;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class MachineGunGuntowerMeta extends AbstractGuntowerMeta {
	@Override
	public AbstractEntity create(Wanderer game) {
		return new MachineGunGuntower(game, this);
	}
}
