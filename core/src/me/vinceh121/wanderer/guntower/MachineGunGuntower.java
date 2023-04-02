package me.vinceh121.wanderer.guntower;

import me.vinceh121.wanderer.Wanderer;

public class MachineGunGuntower extends AbstractGuntower {
	private final MachineGunGuntowerMeta meta;

	public MachineGunGuntower(Wanderer game, MachineGunGuntowerMeta meta) {
		super(game, meta);
		this.meta = meta;
	}

	@Override
	public void fire() {
	}
	
	@Override
	public MachineGunGuntowerMeta getMeta() {
		return meta;
	}
}
