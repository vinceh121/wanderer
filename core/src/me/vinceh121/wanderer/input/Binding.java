package me.vinceh121.wanderer.input;

import java.util.Objects;

public class Binding {
	private int key;
	private DeviceType deviceType;

	public Binding() {
	}

	public Binding(final int key, final DeviceType deviceType) {
		this.key = key;
		this.deviceType = deviceType;
	}

	public int getKey() {
		return this.key;
	}

	public void setKey(final int key) {
		this.key = key;
	}

	public DeviceType getDeviceType() {
		return this.deviceType;
	}

	public void setDeviceType(final DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.deviceType, this.key);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Binding other = (Binding) obj;
		return this.deviceType == other.deviceType && this.key == other.key;
	}

	@Override
	public String toString() {
		return "Binding [key=" + this.key + ", deviceType=" + this.deviceType + "]";
	}

	public enum DeviceType {
		KEYBOARD, MOUSE, MOUSE_WHEEL, CONTROLLER;
	}
}
