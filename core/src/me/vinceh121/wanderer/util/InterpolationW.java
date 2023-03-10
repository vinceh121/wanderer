package me.vinceh121.wanderer.util;

import com.badlogic.gdx.math.Interpolation;

public final class InterpolationW {
	public static final Interpolation NEAREST = new Interpolation() {
		@Override
		public float apply(float a) {
			if (a < 0.5f) {
				return 0f;
			} else {
				return 1f;
			}
		}
	};
}
