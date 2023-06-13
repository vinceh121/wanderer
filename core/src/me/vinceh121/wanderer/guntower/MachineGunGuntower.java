package me.vinceh121.wanderer.guntower;

import static me.vinceh121.wanderer.i18n.I18N.gettext;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.ILivingEntity;
import me.vinceh121.wanderer.math.Segment3;
import me.vinceh121.wanderer.util.MathUtilsW;

public class MachineGunGuntower extends AbstractGuntower {
	private final MachineGunGuntowerMeta meta;
	private final Array<MachineGunTurret> turrets = new Array<>();
	private float fireTimeout;

	public MachineGunGuntower(Wanderer game, MachineGunGuntowerMeta meta) {
		super(game, meta);
		this.meta = meta;

		List<Vector3> turretPos = new ArrayList<>(this.meta.getTurrets().size);
		for (MachineGunTurret turret : this.meta.getTurrets()) {
			this.turrets.add(new MachineGunTurret(turret));
			turretPos.add(turret.getRelativeTransform().getTranslation(new Vector3()));
		}
		// moves the emitter to the average position of all the turrets
		this.fireSoundEmitter.setRelativePosition(MathUtilsW.average(turretPos));
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
			AbstractEntity e = this.game.getEntity(cb.getCollisionObject().getUserIndex());
			if (e instanceof ILivingEntity) {
				((ILivingEntity) e).damage(turret.getDamage(), turret.getType());
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
	public String getName() {
		return gettext("Gun tower");
	}

	@Override
	public MachineGunGuntowerMeta getMeta() {
		return meta;
	}

	public Array<MachineGunTurret> getTurrets() {
		return turrets;
	}
}
