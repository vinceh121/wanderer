package me.vinceh121.wanderer.animation;

import java.util.Objects;

import com.badlogic.gdx.math.Matrix4;

public class TransformKeyFrame {
	private final Matrix4 transform = new Matrix4();
	/**
	 * In seconds
	 */
	private float time;

	public TransformKeyFrame() {
	}

	public TransformKeyFrame(final Matrix4 transform, final float time) {
		this.transform.set(transform);
		this.time = time;
	}

	public float getTime() {
		return this.time;
	}

	public void setTime(final float time) {
		this.time = time;
	}

	public Matrix4 getTransform() {
		return this.transform;
	}

	public void setTransform(final Matrix4 transform) {
		this.transform.set(transform);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.time, this.transform);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final TransformKeyFrame other = (TransformKeyFrame) obj;
		return Float.floatToIntBits(this.time) == Float.floatToIntBits(other.time)
				&& Objects.equals(this.transform, other.transform);
	}

	@Override
	public String toString() {
		return "TransformKeyFrame [transform=" + this.transform + ", time=" + this.time + "]";
	}
}
