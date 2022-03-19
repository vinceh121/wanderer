package me.vinceh121.wanderer.building;

import com.badlogic.gdx.math.Vector3;

public class Slot {
	private SlotType type;
	/**
	 * Slot's location relative to the island's origin
	 */
	private Vector3 location;

	public Slot() {
	}

	public Slot(SlotType type, Vector3 location) {
		this.type = type;
		this.location = location;
	}

	/**
	 * @return the type
	 */
	public SlotType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(SlotType type) {
		this.type = type;
	}

	/**
	 * Slot's location relative to the island's origin
	 *
	 * @return the location
	 */
	public Vector3 getLocation() {
		return location;
	}

	/**
	 * Slot's location relative to the island's origin
	 *
	 * @param location the location to set
	 */
	public void setLocation(Vector3 location) {
		this.location = location;
	}
}
