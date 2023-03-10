package me.vinceh121.wanderer.animation;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

public class TransformAnimation {
	private final SortedSet<TransformKeyFrame> keys = new TreeSet<>((k1, k2) -> Float.compare(k1.getTime(), k2.getTime()));
	private Interpolation inter;

	public Interpolation getInter() {
		return this.inter;
	}

	public void setInter(final Interpolation inter) {
		this.inter = inter;
	}

	public boolean add(final TransformKeyFrame e) {
		return this.keys.add(e);
	}

	public SortedSet<TransformKeyFrame> getKeys() {
		return this.keys;
	}

	public float getTotalTime() {
		return this.keys.last().getTime();
	}

	public Matrix4 valueAt(final float time) {
		final TransformKeyFrame first = this.keys.first();
		if (first.getTime() >= time) {
			return first.getValue().cpy();
		}
		final TransformKeyFrame last = this.keys.last();
		if (last.getTime() <= time) {
			return last.getValue().cpy();
		}

		TransformKeyFrame prev = first;
		TransformKeyFrame next = null;
		for (final TransformKeyFrame k : this.keys) {
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
		return TransformAnimation.interpolate(prev.getValue(),
				next.getValue(),
				MathUtils.norm(prev.getTime(), next.getTime(), time),
				this.inter);
	}

	public static TransformAnimation distributed(final float totalTime, final Matrix4... transforms) {
		final TransformAnimation anim = new TransformAnimation();
		for (int i = 0; i < transforms.length; i++) {
			final TransformKeyFrame key = new TransformKeyFrame();
			key.setValue(transforms[i]);
			key.setTime(i * totalTime / transforms.length);
			anim.add(key);
		}
		return anim;
	}

	public static Matrix4 interpolate(final Matrix4 k1, final Matrix4 k2, final float alpha, final Interpolation inter) {
		Objects.requireNonNull(inter);
		final float[] arr = new float[16];
		for (int i = 0; i < 16; i++) {
			arr[i] = inter.apply(k1.val[i], k2.val[i], alpha);
		}
		return new Matrix4(arr);
	}
}
