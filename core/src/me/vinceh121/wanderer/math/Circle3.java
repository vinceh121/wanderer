package me.vinceh121.wanderer.math;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.util.MathUtilsW;

public class Circle3 {
	private final Vector3 center = new Vector3();
	private final Quaternion rotation = new Quaternion();
	private float radius;

	public Vector3 getPointOn(float radAngle, float frac) {
		Vector2 d2 = MathUtilsW.setFromPolar(new Vector2(), radAngle);
		d2.scl(this.radius * frac);
		Vector3 d3 = new Vector3(d2, 0);
		d3.mul(this.rotation);
		d3.add(this.center);
		return d3;
	}

	public float getRadius() {
		return radius;
	}

	public Circle3 setRadius(float radius) {
		this.radius = radius;
		return this;
	}

	public Vector3 getCenter() {
		return center;
	}

	public Circle3 setCenter(Vector3 pos) {
		this.center.set(pos);
		return this;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public Circle3 setRotation(Quaternion q) {
		this.rotation.set(q);
		return this;
	}
}
