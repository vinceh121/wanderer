package me.vinceh121.wanderer.entity.plane;

public class PlaneSpeedProfile {
	private float minSpeed = Float.NaN, maxSpeed = Float.NaN, acceleration = Float.NaN, decceleration = Float.NaN,
			maxPitch = Float.NaN, pitchSpeed = Float.NaN, maxYaw = Float.NaN, yawSpeed = Float.NaN, maxRoll = Float.NaN,
			rollTime = Float.NaN;

	public PlaneSpeedProfile() {
	}

	public PlaneSpeedProfile(final PlaneSpeedProfile from) {
		this.minSpeed = from.minSpeed;
		this.maxSpeed = from.maxSpeed;
		this.acceleration = from.acceleration;
		this.decceleration = from.decceleration;
		this.maxPitch = from.maxPitch;
		this.pitchSpeed = from.pitchSpeed;
		this.maxYaw = from.maxYaw;
		this.yawSpeed = from.yawSpeed;
		this.maxRoll = from.maxRoll;
		this.rollTime = from.rollTime;
	}

	public float getMinSpeed() {
		return this.minSpeed;
	}

	public void setMinSpeed(final float minSpeed) {
		this.minSpeed = minSpeed;
	}

	public float getMaxSpeed() {
		return this.maxSpeed;
	}

	public void setMaxSpeed(final float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public float getAcceleration() {
		return this.acceleration;
	}

	public void setAcceleration(final float acceleration) {
		this.acceleration = acceleration;
	}

	public float getDecceleration() {
		return this.decceleration;
	}

	public void setDecceleration(final float decceleration) {
		this.decceleration = decceleration;
	}

	public float getMaxPitch() {
		return this.maxPitch;
	}

	public void setMaxPitch(final float maxPitch) {
		this.maxPitch = maxPitch;
	}

	public float getPitchSpeed() {
		return this.pitchSpeed;
	}

	public void setPitchSpeed(final float pitchSpeed) {
		this.pitchSpeed = pitchSpeed;
	}

	public float getMaxYaw() {
		return this.maxYaw;
	}

	public void setMaxYaw(final float maxYaw) {
		this.maxYaw = maxYaw;
	}

	public float getYawSpeed() {
		return this.yawSpeed;
	}

	public void setYawSpeed(final float yawSpeed) {
		this.yawSpeed = yawSpeed;
	}

	public float getMaxRoll() {
		return this.maxRoll;
	}

	public void setMaxRoll(final float maxRoll) {
		this.maxRoll = maxRoll;
	}

	public float getRollTime() {
		return this.rollTime;
	}

	public void setRollTime(final float rollTime) {
		this.rollTime = rollTime;
	}
}
