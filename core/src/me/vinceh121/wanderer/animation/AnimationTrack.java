package me.vinceh121.wanderer.animation;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import com.badlogic.gdx.math.Interpolation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.vinceh121.wanderer.util.InterpolationW;

public class AnimationTrack<T extends KeyFrame<V>, V> {
	private final NavigableMap<Float, T> keyFrames = new TreeMap<>();
	private EnumInterpolation interpolation;

	public AnimationTrack() {
	}

	@JsonCreator
	public AnimationTrack(@JsonProperty("keys") List<T> list, @JsonProperty("interpolation") EnumInterpolation inter) {
		for (T frame : list) {
			this.keyFrames.put(frame.getTime(), frame);
		}
		this.interpolation = inter;
	}

	public NavigableMap<Float, T> getKeyFrames() {
		return keyFrames;
	}

	public EnumInterpolation getInterpolation() {
		return interpolation;
	}

	public void setInterpolation(EnumInterpolation interpolation) {
		this.interpolation = interpolation;
	}

	public NavigableMap<Float, T> inBetween(float startTime, float endTime) {
		return this.keyFrames.subMap(startTime, false, endTime, false);
	}

	public Pair<T, T> getKeyFramesAt(float time) {
		final Entry<Float, T> leftEntry = this.keyFrames.floorEntry(time);
		final Entry<Float, T> rightEntry = this.keyFrames.ceilingEntry(time);
		return Pair.of(leftEntry == null ? null : leftEntry.getValue(),
				rightEntry == null ? null : rightEntry.getValue());
	}

	protected float getAlpha(Pair<T, T> pair, float time) {
		return (time - pair.getLeft().getTime()) / (pair.getRight().getTime() - pair.getLeft().getTime());
	}

	protected Interpolation getDefaultInterpolation() {
		return Interpolation.linear;
	}

	public V interpolate(float time) {
		Pair<T, T> pair = this.getKeyFramesAt(time);
		if (pair.getLeft() == null && pair.getRight() != null) {
			return pair.getRight().getValue();
		}
		if (pair.getLeft() != null && pair.getRight() == null) {
			return pair.getLeft().getValue();
		}
		if (pair.getLeft() == null && pair.getRight() == null) {
			return null;
		}
		return pair.getLeft()
			.interpolate(pair.getRight().getValue(),
					this.interpolation == null ? this.getDefaultInterpolation() : this.interpolation.inter,
					getAlpha(pair, time));
	}

	public float getStartTime() {
		if (this.keyFrames.size() == 0) {
			return Float.NaN;
		}
		return this.keyFrames.firstKey();
	}

	public float getEndTime() {
		if (this.keyFrames.size() == 0) {
			return Float.NaN;
		}
		return this.keyFrames.lastKey();
	}

	public enum EnumInterpolation {
		NEAREST(InterpolationW.NEAREST),
		LINEAR(Interpolation.linear),
		SPHERICAL,
		BOUNCE(Interpolation.bounce),
		CIRCLE(Interpolation.circle),
		FADE(Interpolation.fade),
		ELASTIC(Interpolation.elastic);

		private final Interpolation inter;

		private EnumInterpolation() {
			this(null);
		}

		private EnumInterpolation(Interpolation inter) {
			this.inter = inter;
		}

		public Interpolation getInter() {
			return inter;
		}
	}
}
