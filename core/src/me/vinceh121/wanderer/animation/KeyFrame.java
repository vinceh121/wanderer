package me.vinceh121.wanderer.animation;

import java.util.Objects;

import com.badlogic.gdx.math.Interpolation;

public abstract class KeyFrame<T> {
	private float time;
	private T value;

	public KeyFrame() {
	}

	public KeyFrame(float time, T value) {
		this.time = time;
		this.value = value;
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public abstract T interpolate(T other, Interpolation i, float alpha);
	
	@Override
	public int hashCode() {
		return Objects.hash(time, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyFrame<?> other = (KeyFrame<?>) obj;
		return Float.floatToIntBits(time) == Float.floatToIntBits(other.time) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return getClass().getName() + " [time=" + time + ", value=" + value + "]";
	}

}
