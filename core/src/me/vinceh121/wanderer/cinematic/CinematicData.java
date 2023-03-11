package me.vinceh121.wanderer.cinematic;

import java.util.Arrays;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.annotation.JsonIgnore;

import me.vinceh121.wanderer.animation.AnimationTrack;
import me.vinceh121.wanderer.animation.QuaternionKeyFrame;
import me.vinceh121.wanderer.animation.Vector3KeyFrame;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class CinematicData {
	private String symbolicName;
	@JsonIgnore
	private AbstractEntity cacheEntity;
	private AnimationTrack<Vector3KeyFrame, Vector3> position = new AnimationTrack<>();
	private AnimationTrack<QuaternionKeyFrame, Quaternion> rotation = new AnimationTrack<>();
	private AnimationTrack<Vector3KeyFrame, Vector3> scale = new AnimationTrack<>();
	private AnimationTrack<ActionKeyFrame, Void> actions = new AnimationTrack<>();

	@JsonIgnore
	public float getStartTime() {
		return Arrays.asList(this.position, this.rotation, this.scale, this.actions)
			.stream()
			.filter(e -> e != null)
			.map(AnimationTrack::getStartTime)
			.filter(t -> !Float.isNaN(t))
			.min(Float::compare)
			.get();
	}

	@JsonIgnore
	public float getEndTime() {
		return Arrays.asList(this.position, this.rotation, this.scale, this.actions)
			.stream()
			.filter(e -> e != null)
			.map(AnimationTrack::getEndTime)
			.filter(t -> !Float.isNaN(t))
			.max(Float::compare)
			.get();
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public AbstractEntity getCacheEntity() {
		return cacheEntity;
	}

	public void setCacheEntity(AbstractEntity cacheEntity) {
		this.cacheEntity = cacheEntity;
	}

	public AnimationTrack<Vector3KeyFrame, Vector3> getPosition() {
		return position;
	}

	public void setPosition(AnimationTrack<Vector3KeyFrame, Vector3> position) {
		this.position = position;
	}

	public AnimationTrack<QuaternionKeyFrame, Quaternion> getRotation() {
		return rotation;
	}

	public void setRotation(AnimationTrack<QuaternionKeyFrame, Quaternion> rotation) {
		this.rotation = rotation;
	}

	public AnimationTrack<Vector3KeyFrame, Vector3> getScale() {
		return scale;
	}

	public void setScale(AnimationTrack<Vector3KeyFrame, Vector3> scale) {
		this.scale = scale;
	}

	public AnimationTrack<ActionKeyFrame, Void> getActions() {
		return actions;
	}

	public void setActions(AnimationTrack<ActionKeyFrame, Void> actions) {
		this.actions = actions;
	}
}
