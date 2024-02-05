package me.vinceh121.wanderer.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public final class MathUtilsW {
	public static Vector3 preciseSetFromSpherical(final Vector3 v, float azimuthalAngle, float polarAngle) {
		double cosPolar = Math.cos(polarAngle);
		double sinPolar = Math.sin(polarAngle);

		double cosAzim = Math.cos(azimuthalAngle);
		double sinAzim = Math.sin(azimuthalAngle);

		return v.set((float) (cosAzim * sinPolar), (float) (sinAzim * sinPolar), (float) cosPolar);
	}

	public static Vector3 randomDirectionAround(final Vector3 orig, final float maxDeviation) {
		final Vector3 deviation = new Vector3();
		deviation.setFromSpherical(MathUtils.random(MathUtils.PI2), MathUtils.random(MathUtils.PI));
		deviation.slerp(deviation, 1 - maxDeviation);
		return deviation;
	}

	public static Vector3 average(final Iterable<Vector3> vecs) {
		int count = 0;
		final Vector3 res = new Vector3();

		for (final Vector3 v : vecs) {
			res.add(v);
			count++;
		}

		res.x /= count;
		res.y /= count;
		res.z /= count;
		return res;
	}

	public static Vector2 setFromPolar(final Vector2 v, final float rad) {
		v.x = MathUtils.cos(rad);
		v.y = MathUtils.sin(rad);
		return v;
	}

	public static float getSphericalPolar(final float z) {
		return MathUtils.acos(z);
	}

	public static float getSphericalAzimuth(final float x, final float y) {
		return Math.signum(y) * MathUtils.acos(x / (float) Math.sqrt(x * x + y * y));
	}

	public static Vector3 fixNaN(final Vector3 v, final float val) {
		if (Float.isNaN(v.x)) {
			v.x = val;
		}
		if (Float.isNaN(v.y)) {
			v.y = val;
		}
		if (Float.isNaN(v.z)) {
			v.z = val;
		}
		return v;
	}

	public static Vector3 fixInfinity(final Vector3 v, final float val) {
		if (Float.isInfinite(v.x)) {
			v.x = val;
		}
		if (Float.isInfinite(v.y)) {
			v.y = val;
		}
		if (Float.isInfinite(v.z)) {
			v.z = val;
		}
		return v;
	}

	public static Quaternion fixNaNIdt(final Quaternion q) {
		if (Float.isNaN(q.w) || Float.isNaN(q.x) || Float.isNaN(q.y) || Float.isNaN(q.z)) {
			q.idt();
		}
		return q;
	}

	public static Matrix4 setRotation(final Matrix4 trans, final Quaternion rot) {
		final Vector3 pos = new Vector3();
		final Vector3 scale = new Vector3();
		trans.getTranslation(pos);
		trans.getScale(scale);

		trans.set(pos, rot, scale);
		return trans;
	}

	public static Matrix4 setScale(final Matrix4 trans, final Vector3 scale) {
		final Vector3 pos = new Vector3();
		final Quaternion rot = new Quaternion();
		trans.getTranslation(pos);
		trans.getRotation(rot);

		trans.set(pos, rot, scale);
		return trans;
	}

	public static String toString(final Vector3 vec3) {
		return "( " + vec3.x + ",\t" + vec3.y + ",\t" + vec3.z + " )";
	}
}
