package me.vinceh121.wanderer.animation;

import java.util.Objects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class TransformKeyFrame extends KeyFrame<Matrix4> {
	private final Matrix4 transform = new Matrix4();

	public TransformKeyFrame() {
	}

	public TransformKeyFrame(final Matrix4 transform, final float time) {
		this.transform.set(transform);
		this.setTime(time);
	}

	@Override
	public Matrix4 interpolate(final Matrix4 other, final Interpolation i, final float alpha) {
		assert i != null && i == Interpolation.linear : "TransformKeyFrame only supports linear interpolation";
		return this.getValue().cpy().lerp(other, alpha);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(this.transform);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final TransformKeyFrame other = (TransformKeyFrame) obj;
		return Objects.equals(this.transform, other.transform);
	}

	public void action(final Wanderer game, final AbstractEntity target, final float time) {
	}
}
