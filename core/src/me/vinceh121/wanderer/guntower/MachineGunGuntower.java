package me.vinceh121.wanderer.guntower;

import me.vinceh121.wanderer.Wanderer;

public class MachineGunGuntower extends AbstractGuntower {
	private final MachineGunGuntowerMeta meta;
	private float fireTimeout;

	public MachineGunGuntower(Wanderer game, MachineGunGuntowerMeta meta) {
		super(game, meta);
		this.meta = meta;
	}

	@Override
	public void fire() {
		if (fireTimeout != 0) {
			return;
		}

		this.fireSoundEmitter.play();
		this.fireTimeout = 0.085f;
	}

	@Override
	public void tick(float delta) {
		super.tick(delta);
		this.fireTimeout -= delta;
		if (this.fireTimeout < 0) {
			this.fireTimeout = 0;
		}
	}

	@Override
	public MachineGunGuntowerMeta getMeta() {
		return meta;
	}
}
