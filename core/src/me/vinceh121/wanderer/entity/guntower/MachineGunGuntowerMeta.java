package me.vinceh121.wanderer.entity.guntower;

import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class MachineGunGuntowerMeta extends AbstractGuntowerMeta {
	private final Array<MachineGunTurret> turrets = new Array<>();

	public Array<MachineGunTurret> getTurrets() {
		return this.turrets;
	}

	@Override
	public AbstractEntity create(final Wanderer game) {
		return new MachineGunGuntower(game, this);
	}
}
