package me.vinceh121.wanderer.entity.plane;

public class PlaneSpeedProfile {
	private float minSpeed = Float.NaN, maxSpeed = Float.NaN, acceleration = Float.NaN, decceleration = Float.NaN,
			maxPitch = Float.NaN, pitchSpeed = Float.NaN, maxYaw = Float.NaN, yawSpeed = Float.NaN, maxRoll = Float.NaN,
			rollTime = Float.NaN;

	public PlaneSpeedProfile() {
	}

	public PlaneSpeedProfile(PlaneSpeedProfile from) {
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
		return minSpeed;
	}

	public void setMinSpeed(float minSpeed) {
		this.minSpeed = minSpeed;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public float getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(float acceleration) {
		this.acceleration = acceleration;
	}

	public float getDecceleration() {
		return decceleration;
	}

	public void setDecceleration(float decceleration) {
		this.decceleration = decceleration;
	}

	public float getMaxPitch() {
		return maxPitch;
	}

	public void setMaxPitch(float maxPitch) {
		this.maxPitch = maxPitch;
	}

	public float getPitchSpeed() {
		return pitchSpeed;
	}

	public void setPitchSpeed(float pitchSpeed) {
		this.pitchSpeed = pitchSpeed;
	}

	public float getMaxYaw() {
		return maxYaw;
	}

	public void setMaxYaw(float maxYaw) {
		this.maxYaw = maxYaw;
	}

	public float getYawSpeed() {
		return yawSpeed;
	}

	public void setYawSpeed(float yawSpeed) {
		this.yawSpeed = yawSpeed;
	}

	public float getMaxRoll() {
		return maxRoll;
	}

	public void setMaxRoll(float maxRoll) {
		this.maxRoll = maxRoll;
	}

	public float getRollTime() {
		return rollTime;
	}

	public void setRollTime(float rollTime) {
		this.rollTime = rollTime;
	}
}
