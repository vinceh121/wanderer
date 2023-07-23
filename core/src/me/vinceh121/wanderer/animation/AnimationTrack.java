package me.vinceh121.wanderer.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import com.badlogic.gdx.math.Interpolation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.vinceh121.wanderer.util.InterpolationW;

public class AnimationTrack<T extends KeyFrame<V>, V> {
	@JsonIgnore
	private final NavigableMap<Float, T> keyFrames = new TreeMap<>(AnimationTrack::compareKeys);
	@JsonInclude(value = Include.NON_NULL)
	private EnumInterpolation interpolation;

	public AnimationTrack() {
	}

	@JsonCreator
	public AnimationTrack(@JsonProperty("keys") final List<T> list, @JsonProperty("interpolation") final EnumInterpolation inter) {
		for (final T frame : list) {
			this.addKeyframe(frame);
		}
		this.interpolation = inter;
	}

	public void addKeyframe(final T frame) {
		this.keyFrames.put(frame.getTime(), frame);
	}

	@JsonProperty("keys")
	public List<T> getKeyFrames() {
		return new ArrayList<>(this.keyFrames.values());
	}

	@JsonIgnore
	public NavigableMap<Float, T> getKeyFramesMap() {
		return this.keyFrames;
	}

	public EnumInterpolation getInterpolation() {
		return this.interpolation;
	}

	public void setInterpolation(final EnumInterpolation interpolation) {
		this.interpolation = interpolation;
	}

	public NavigableMap<Float, T> inBetween(final float startTime, final float endTime) {
		return this.keyFrames.subMap(startTime, true, endTime, false);
	}

	public Pair<T, T> getKeyFramesAt(final float time) {
		final Entry<Float, T> leftEntry = this.keyFrames.floorEntry(time);
		final Entry<Float, T> rightEntry = this.keyFrames.ceilingEntry(time);
		return Pair.of(leftEntry == null ? null : leftEntry.getValue(),
				rightEntry == null ? null : rightEntry.getValue());
	}

	protected float getAlpha(final Pair<T, T> pair, final float time) {
		return (time - pair.getLeft().getTime()) / (pair.getRight().getTime() - pair.getLeft().getTime());
	}

	protected Interpolation getDefaultInterpolation() {
		return Interpolation.linear;
	}

	public V interpolate(final float time) {
		final Pair<T, T> pair = this.getKeyFramesAt(time);
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
					this.getAlpha(pair, time));
	}

	@JsonIgnore
	public float getStartTime() {
		if (this.keyFrames.size() == 0) {
			return Float.NaN;
		}
		return this.keyFrames.firstKey();
	}

	@JsonIgnore
	public T getFirstKeyFrame() {
		return this.keyFrames.firstEntry().getValue();
	}

	@JsonIgnore
	public float getEndTime() {
		if (this.keyFrames.size() == 0) {
			return Float.NaN;
		}
		return this.keyFrames.lastKey();
	}

	@JsonIgnore
	public T getLastKeyFrame() {
		return this.keyFrames.lastEntry().getValue();
	}

	// HACK: we don't want to prevent keys being removed on duplicate timings, so we
	// make a float comparator that cannot return 0
	private static int compareKeys(final Float f1, final Float f2) {
		final int fc = Float.compare(f1, f2);
		if (fc == 0) {
			// can't return 1 here! otherwise we get submaps where fromKey > toKey
			return -1;
		}
		return fc;
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

		EnumInterpolation() {
			this(null);
		}

		EnumInterpolation(final Interpolation inter) {
			this.inter = inter;
		}

		public Interpolation getInter() {
			return this.inter;
		}
	}
}
