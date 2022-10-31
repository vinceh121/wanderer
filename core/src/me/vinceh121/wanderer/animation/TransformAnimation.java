package me.vinceh121.wanderer.animation;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

public class TransformAnimation {
	private final SortedSet<TransformKeyFrame> keys = new TreeSet<>((k1, k2) -> {
		return Float.compare(k1.getTime(), k2.getTime());
	});
	private Interpolation inter;

	public Interpolation getInter() {
		return inter;
	}

	public void setInter(Interpolation inter) {
		this.inter = inter;
	}

	public boolean add(TransformKeyFrame e) {
		return keys.add(e);
	}

	public SortedSet<TransformKeyFrame> getKeys() {
		return keys;
	}

	public float getTotalTime() {
		return this.keys.last().getTime();
	}

	public Matrix4 valueAt(float time) {
		final TransformKeyFrame first = this.keys.first();
		if (first.getTime() >= time) {
			return first.getTransform().cpy();
		}
		final TransformKeyFrame last = this.keys.last();
		if (last.getTime() <= time) {
			return last.getTransform().cpy();
		}

		TransformKeyFrame prev = first;
		TransformKeyFrame next = null;
		for (TransformKeyFrame k : this.keys) {
			if (prev.getTime() < time && k.getTime() > time) {
				next = k;
				break;
			}
			prev = k;
		}
		if (next == null) {
			// shouldn't happen
			throw new IllegalStateException("Couldn't find valid key");
		}
		return interpolate(prev.getTransform(),
				next.getTransform(),
				MathUtils.norm(prev.getTime(), next.getTime(), time),
				this.inter);
	}

	public static TransformAnimation distributed(float totalTime, Matrix4... transforms) {
		final TransformAnimation anim = new TransformAnimation();
		for (int i = 0; i < transforms.length; i++) {
			final TransformKeyFrame key = new TransformKeyFrame();
			key.setTransform(transforms[i]);
			key.setTime(i * totalTime / transforms.length);
			anim.add(key);
		}
		return anim;
	}

	public static Matrix4 interpolate(Matrix4 k1, Matrix4 k2, float alpha, Interpolation inter) {
		Objects.requireNonNull(inter);
		float[] arr = new float[16];
		for (int i = 0; i < 16; i++) {
			arr[i] = inter.apply(k1.val[i], k2.val[i], alpha);
		}
		return new Matrix4(arr);
	}
}
