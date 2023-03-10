package me.vinceh121.wanderer.animation;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Quaternion;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class QuaternionKeyFrame extends KeyFrame<Quaternion> {
	@Override
	public Quaternion interpolate(Quaternion other, Interpolation i, float alpha) {
		assert i != null : "QuaternionKeyFrame only supports internal SLERP, set interpolation to null";
		return this.getValue().slerp(other, alpha);
	}

	public void action(Wanderer game, AbstractEntity target, float time) {
	}
}
