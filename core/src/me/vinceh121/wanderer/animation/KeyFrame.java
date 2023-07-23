package me.vinceh121.wanderer.animation;

import java.util.Objects;

import com.badlogic.gdx.math.Interpolation;

public abstract class KeyFrame<T> {
	private float time;
	private T value;

	public KeyFrame() {
	}

	public KeyFrame(final float time, final T value) {
		this.time = time;
		this.value = value;
	}

	public float getTime() {
		return this.time;
	}

	public void setTime(final float time) {
		this.time = time;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(final T value) {
		this.value = value;
	}

	public abstract T interpolate(T other, Interpolation i, float alpha);

	@Override
	public int hashCode() {
		return Objects.hash(this.time, this.value);
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
		final KeyFrame<?> other = (KeyFrame<?>) obj;
		return Float.floatToIntBits(this.time) == Float.floatToIntBits(other.time) && Objects.equals(this.value, other.value);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + " [time=" + this.time + ", value=" + this.value + "]";
	}

}
