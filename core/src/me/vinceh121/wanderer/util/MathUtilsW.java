package me.vinceh121.wanderer.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public final class MathUtilsW {
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
