package me.vinceh121.wanderer.input;

import java.util.Objects;

public class Binding {
	private int key;
	private DeviceType deviceType;

	public Binding() {
	}

	public Binding(int key, DeviceType deviceType) {
		this.key = key;
		this.deviceType = deviceType;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(deviceType, key);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Binding other = (Binding) obj;
		return deviceType == other.deviceType && key == other.key;
	}

	public enum DeviceType {
		KEYBOARD, MOUSE, MOUSE_WHEEL, CONTROLLER;
	}
}
