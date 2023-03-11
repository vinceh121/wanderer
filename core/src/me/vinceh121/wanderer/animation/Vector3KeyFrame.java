package me.vinceh121.wanderer.animation;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class Vector3KeyFrame extends KeyFrame<Vector3> {
	public Vector3KeyFrame() {
	}

	public Vector3KeyFrame(float time, Vector3 value) {
		super(time, value);
	}

	@Override
	public Vector3 interpolate(Vector3 other, Interpolation i, float alpha) {
		return this.getValue().cpy().interpolate(other, alpha, i);
	}

	public void action(Wanderer game, AbstractEntity target, float time) {
	}
}
