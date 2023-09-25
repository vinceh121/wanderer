package me.vinceh121.wanderer.guntower;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.combat.BulletEntity;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.ILivingEntity;
import me.vinceh121.wanderer.i18n.I18N;
import me.vinceh121.wanderer.math.Segment3;
import me.vinceh121.wanderer.util.MathUtilsW;

public class MachineGunGuntower extends AbstractGuntower {
	private final MachineGunGuntowerMeta meta;
	private final Array<MachineGunTurret> turrets = new Array<>();
	private float fireTimeout;

	public MachineGunGuntower(final Wanderer game, final MachineGunGuntowerMeta meta) {
		super(game, meta);
		this.meta = meta;

		final List<Vector3> turretPos = new ArrayList<>(this.meta.getTurrets().size);
		for (final MachineGunTurret turret : this.meta.getTurrets()) {
			this.turrets.add(new MachineGunTurret(turret));
			turretPos.add(turret.getRelativeTransform().getTranslation(new Vector3()));
		}
		// moves the emitter to the average position of all the turrets
		this.fireSoundEmitter.setRelativePosition(MathUtilsW.average(turretPos));
	}

	@Override
	public void fire() {
		if (this.fireTimeout != 0) {
			return;
		}

		for (final MachineGunTurret turret : this.turrets) {
			this.fireTurret(turret);
		}

		this.fireSoundEmitter.play();
		this.fireTimeout = 0.085f;

		this.game.shakeCamera(0.5f, 0.25f);
	}

	private void fireTurret(final MachineGunTurret turret) {
		turret.updateTransform(this.getTransform());
		turret.getAbsoluteTransform().rotate(this.getLookRotation());
		final Segment3 seg = turret.calculateRandomBulletPath();

		final ClosestNotMeRayResultCallback cb = new ClosestNotMeRayResultCallback(this.getCollideObject());
		this.game.getBtWorld().rayTest(seg.getStart(), seg.getEnd(), cb);

		if (cb.hasHit()) {
			final AbstractEntity e = this.game.getEntity(cb.getCollisionObject().getUserIndex());
			if (e instanceof ILivingEntity) {
				((ILivingEntity) e).damage(turret.getDamage(), turret.getType());
			}
		}

		final Vector3 hitPoint = new Vector3();
		cb.getHitPointWorld(hitPoint);

		final BulletEntity bullet = new BulletEntity(this.game);
		bullet.setTranslation(seg.getStart());
		final Vector3 direction = seg.getEnd().cpy().sub(seg.getStart()).nor();
		bullet.rotate(new Quaternion().setFromCross(direction, Vector3.Z).conjugate());
		bullet.setDirection(direction);
		bullet.setHasHit(cb.hasHit());

		if (bullet.isHasHit()) {
			bullet.setDistance(seg.getStart()
				.cpy()
				.interpolate(seg.getEnd(), cb.getClosestHitFraction(), Interpolation.linear)
				.dst(seg.getStart()));
		} else {
			bullet.setDistance(seg.length());
		}

		this.game.addEntity(bullet);

		cb.dispose();
	}

	@Override
	public void tick(final float delta) {
		super.tick(delta);
		this.fireTimeout -= delta;
		if (this.fireTimeout < 0) {
			this.fireTimeout = 0;
		}
	}

	@Override
	public Vector3 getAverageTurretPosition() {
		final List<Vector3> pos = new ArrayList<Vector3>(this.turrets.size);

		for (int i = 0; i < this.turrets.size; i++) {
			pos.add(this.turrets.get(i).getRelativeTransform().getTranslation(new Vector3()));
		}

		return MathUtilsW.average(pos);
	}

	@Override
	public String getControlMessage() {
		return I18N.gettext("Gun tower");
	}

	@Override
	public MachineGunGuntowerMeta getMeta() {
		return this.meta;
	}

	public Array<MachineGunTurret> getTurrets() {
		return this.turrets;
	}
}
