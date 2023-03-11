package me.vinceh121.wanderer.cinematic;

import com.badlogic.gdx.math.Interpolation;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.animation.KeyFrame;
import me.vinceh121.wanderer.entity.AbstractEntity;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class ActionKeyFrame extends KeyFrame<Void> {
	
	public ActionKeyFrame() {
	}

	public ActionKeyFrame(float time) {
		super(time, null);
	}

	public abstract void action(Wanderer game, AbstractEntity target, float time);

	@Override
	public Void interpolate(Void other, Interpolation inter, float alpha) {
		return null;
	}
}
