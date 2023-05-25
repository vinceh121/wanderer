package me.vinceh121.wanderer.guntower;

import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.ILivingEntity;
import me.vinceh121.wanderer.math.Segment3;

public class MachineGunGuntower extends AbstractGuntower {
	private final MachineGunGuntowerMeta meta;
	private final Array<MachineGunTurret> turrets = new Array<>();
	private float fireTimeout;

	public MachineGunGuntower(Wanderer game, MachineGunGuntowerMeta meta) {
		super(game, meta);
		this.meta = meta;

		for (MachineGunTurret turret : this.meta.getTurrets()) {
			this.turrets.add(new MachineGunTurret(turret));
		}
	}

	@Override
	public void fire() {
		if (fireTimeout != 0) {
			return;
		}

		for (MachineGunTurret turret : this.turrets) {
			this.fireTurret(turret);
		}

		this.fireSoundEmitter.play();
		this.fireTimeout = 0.085f;
	}

	private void fireTurret(MachineGunTurret turret) {
		turret.updateTransform(getTransform());
		turret.getAbsoluteTransform().rotate(this.getLookRotation());
		Segment3 seg = turret.calculateRandomBulletPath();

		ClosestNotMeRayResultCallback cb = new ClosestNotMeRayResultCallback(getCollideObject());
		game.getBtWorld().rayTest(seg.getStart(), seg.getEnd(), cb);

		if (cb.hasHit()) {
			System.out.println("hit!");
			AbstractEntity e = this.game.getEntity(cb.getCollisionObject().getUserIndex());
			System.out.println(e);
			if (e instanceof ILivingEntity) {
				System.out.println("health before: " + ((ILivingEntity) e).getHealth());
				((ILivingEntity) e).setInvincible(false);
				((ILivingEntity) e).damage(turret.getDamage(), turret.getType());
				System.out.println("health now: " + ((ILivingEntity) e).getHealth());
			}
		}

		cb.dispose();
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

	public Array<MachineGunTurret> getTurrets() {
		return turrets;
	}
}
