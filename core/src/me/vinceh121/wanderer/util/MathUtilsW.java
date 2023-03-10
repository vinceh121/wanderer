package me.vinceh121.wanderer.util;

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
}
