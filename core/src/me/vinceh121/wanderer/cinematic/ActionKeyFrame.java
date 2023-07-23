package me.vinceh121.wanderer.cinematic;

import com.badlogic.gdx.math.Interpolation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.animation.KeyFrame;
import me.vinceh121.wanderer.entity.AbstractEntity;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class ActionKeyFrame extends KeyFrame<Void> {

	public ActionKeyFrame() {
	}

	public ActionKeyFrame(final float time) {
		super(time, null);
	}

	public abstract void action(Wanderer game, CinematicController controller, AbstractEntity target, float time);

	@JsonIgnore
	@Override
	public Void getValue() {
		return super.getValue();
	}

	@JsonIgnore
	@Override
	public void setValue(final Void value) {
		super.setValue(value);
	}

	@Override
	public Void interpolate(final Void other, final Interpolation inter, final float alpha) {
		return null;
	}
}
