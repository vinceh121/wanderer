package me.vinceh121.wanderer.entity.guntower;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.annotation.JsonIgnore;

import me.vinceh121.wanderer.combat.DamageType;
import me.vinceh121.wanderer.math.Circle3;
import me.vinceh121.wanderer.math.Segment3;

public class MachineGunTurret {
	@JsonIgnore
	private final Matrix4 absoluteTransform = new Matrix4();
	private final Matrix4 relativeTransform = new Matrix4();
	private float distance, startRadius, endRadius, damage;
	private DamageType type = DamageType.MACHINE_GUN;

	public MachineGunTurret() {
	}

	public MachineGunTurret(final MachineGunTurret other) {
		this.absoluteTransform.set(other.absoluteTransform);
		this.relativeTransform.set(other.relativeTransform);
		this.distance = other.distance;
		this.startRadius = other.startRadius;
		this.endRadius = other.endRadius;
		this.damage = other.damage;
		this.type = other.type;
	}

	public void updateTransform(final Matrix4 entityTrans) {
		this.absoluteTransform.set(entityTrans);

		this.absoluteTransform.mul(this.relativeTransform);
	}

	public Segment3 calculateRandomBulletPath() {
		return this.calculateBulletPath(MathUtils.random(MathUtils.PI2), MathUtils.random());
	}

	public Segment3 calculateBulletPath(final float angle, final float radiusFrac) {
		// adapt random distribution to avoid concentration of points to center of
		// circle
		// https://www.youtube.com/watch?v=4y_nmpv-9lI
		final float radSqrt = (float) Math.sqrt(radiusFrac);
		final Quaternion rot = this.absoluteTransform.getRotation(new Quaternion());

		final Circle3 near = new Circle3();
		near.setRadius(this.startRadius);
		near.setRotation(rot);
		this.absoluteTransform.getTranslation(near.getCenter());

		final Circle3 far = new Circle3();
		far.setRadius(this.endRadius);
		far.setRotation(rot);
		far.getCenter().set(0, this.distance, 0).mul(rot).add(near.getCenter());

		final Vector3 pointNear = near.getPointOn(angle, radSqrt);
		final Vector3 pointFar = far.getPointOn(angle, radSqrt);

		final Segment3 seg = new Segment3(pointNear, pointFar);
		return seg;
	}

	public float getDistance() {
		return this.distance;
	}

	public void setDistance(final float distance) {
		this.distance = distance;
	}

	public float getStartRadius() {
		return this.startRadius;
	}

	public void setStartRadius(final float startRadius) {
		this.startRadius = startRadius;
	}

	public float getEndRadius() {
		return this.endRadius;
	}

	public void setEndRadius(final float endRadius) {
		this.endRadius = endRadius;
	}

	public float getDamage() {
		return this.damage;
	}

	public void setDamage(final float damage) {
		this.damage = damage;
	}

	public DamageType getType() {
		return this.type;
	}

	public void setType(final DamageType type) {
		this.type = type;
	}

	public Matrix4 getRelativeTransform() {
		return this.relativeTransform;
	}

	public Matrix4 getAbsoluteTransform() {
		return this.absoluteTransform;
	}
}
