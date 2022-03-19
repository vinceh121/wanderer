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

	public Slot(final SlotType type, final Vector3 location) {
		this.type = type;
		this.location = location;
	}

	/**
	 * @return the type
	 */
	public SlotType getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(final SlotType type) {
		this.type = type;
	}

	/**
	 * Slot's location relative to the island's origin
	 *
	 * @return the location
	 */
	public Vector3 getLocation() {
		return this.location;
	}

	/**
	 * Slot's location relative to the island's origin
	 *
	 * @param location the location to set
	 */
	public void setLocation(final Vector3 location) {
		this.location = location;
	}
}
