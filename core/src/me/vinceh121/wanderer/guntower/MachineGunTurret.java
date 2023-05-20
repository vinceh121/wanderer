package me.vinceh121.wanderer.guntower;

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
	private float distance, startRadius, endRadius;
	private DamageType type = DamageType.MACHINE_GUN;

	public MachineGunTurret() {
	}

	public MachineGunTurret(MachineGunTurret other) {
		this.absoluteTransform.set(other.absoluteTransform);
		this.relativeTransform.set(other.relativeTransform);
		this.distance = other.distance;
		this.startRadius = other.startRadius;
		this.endRadius = other.endRadius;
		this.type = other.type;
	}

	public void updateTransform(final Matrix4 entityTrans) {
		this.absoluteTransform.set(entityTrans);

		this.absoluteTransform.mul(this.relativeTransform);
	}

	public Segment3 calculateRandomBulletPath() {
		return this.calculateBulletPath(MathUtils.random(MathUtils.PI2), MathUtils.random());
	}

	public Segment3 calculateBulletPath(float angle, float radiusFrac) {
		// adapt random distribution to avoid concentration of points to center of circle
		// https://www.youtube.com/watch?v=4y_nmpv-9lI
		float radSqrt = (float) Math.sqrt(radiusFrac);
		Quaternion rot = this.absoluteTransform.getRotation(new Quaternion());

		Circle3 near = new Circle3();
		near.setRadius(this.startRadius);
		near.setRotation(rot);
		this.absoluteTransform.getTranslation(near.getCenter());

		Circle3 far = new Circle3();
		far.setRadius(this.endRadius);
		far.setRotation(rot);
		far.getCenter().set(0, this.distance, 0).mul(rot).add(near.getCenter());

		Vector3 pointNear = near.getPointOn(angle, radSqrt);
		Vector3 pointFar = far.getPointOn(angle, radSqrt);

		Segment3 seg = new Segment3(pointNear, pointFar);
		return seg;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getStartRadius() {
		return startRadius;
	}

	public void setStartRadius(float startRadius) {
		this.startRadius = startRadius;
	}

	public float getEndRadius() {
		return endRadius;
	}

	public void setEndRadius(float endRadius) {
		this.endRadius = endRadius;
	}

	public DamageType getType() {
		return type;
	}

	public void setType(DamageType type) {
		this.type = type;
	}

	public Matrix4 getRelativeTransform() {
		return relativeTransform;
	}

	public Matrix4 getAbsoluteTransform() {
		return absoluteTransform;
	}
}
