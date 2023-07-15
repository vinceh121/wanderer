package me.vinceh121.wanderer.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public final class MathUtilsW {
	public static Vector3 randomDirectionAround(Vector3 orig, float maxDeviation) {
		Vector3 deviation = new Vector3();
		deviation.setFromSpherical(MathUtils.random(MathUtils.PI2), MathUtils.random(MathUtils.PI));
		deviation.slerp(deviation, 1 - maxDeviation);
		return deviation;
	}

	public static Vector3 average(Iterable<Vector3> vecs) {
		int count = 0;
		Vector3 res = new Vector3();

		for (Vector3 v : vecs) {
			res.add(v);
			count++;
		}

		res.x /= count;
		res.y /= count;
		res.z /= count;
		return res;
	}

	public static Vector2 setFromPolar(Vector2 v, float rad) {
		v.x = MathUtils.cos(rad);
		v.y = MathUtils.sin(rad);
		return v;
	}

	public static Vector3 fixNaN(Vector3 v, float val) {
		if (Float.isNaN(v.x))
			v.x = val;
		if (Float.isNaN(v.y))
			v.y = val;
		if (Float.isNaN(v.z))
			v.z = val;
		return v;
	}

	public static Vector3 fixInfinity(Vector3 v, float val) {
		if (Float.isInfinite(v.x))
			v.x = val;
		if (Float.isInfinite(v.y))
			v.y = val;
		if (Float.isInfinite(v.z))
			v.z = val;
		return v;
	}

	public static Quaternion fixNaNIdt(Quaternion q) {
		if (Float.isNaN(q.w) || Float.isNaN(q.x) || Float.isNaN(q.y) || Float.isNaN(q.z)) {
			q.idt();
		}
		return q;
	}

	public static Matrix4 setRotation(Matrix4 trans, Quaternion rot) {
		Vector3 pos = new Vector3();
		Vector3 scale = new Vector3();
		trans.getTranslation(pos);
		trans.getScale(scale);

		trans.set(pos, rot, scale);
		return trans;
	}

	public static Matrix4 setScale(Matrix4 trans, Vector3 scale) {
		Vector3 pos = new Vector3();
		Quaternion rot = new Quaternion();
		trans.getTranslation(pos);
		trans.getRotation(rot);

		trans.set(pos, rot, scale);
		return trans;
	}
}
