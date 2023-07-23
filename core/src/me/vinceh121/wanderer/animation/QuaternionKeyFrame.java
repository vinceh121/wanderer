package me.vinceh121.wanderer.animation;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Quaternion;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class QuaternionKeyFrame extends KeyFrame<Quaternion> {
	public QuaternionKeyFrame() {
	}

	public QuaternionKeyFrame(final float time, final Quaternion value) {
		super(time, value);
	}

	@Override
	public Quaternion interpolate(final Quaternion other, final Interpolation i, final float alpha) {
		assert i != null : "QuaternionKeyFrame only supports internal SLERP, set interpolation to null";
		return this.getValue().cpy().slerp(other, alpha);
	}

	public void action(final Wanderer game, final AbstractEntity target, final float time) {
	}
}
