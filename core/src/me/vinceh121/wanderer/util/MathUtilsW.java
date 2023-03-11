package me.vinceh121.wanderer.util;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public final class MathUtilsW {
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
}
