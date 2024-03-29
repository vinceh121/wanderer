package me.vinceh121.wanderer.building;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Slot {
	private SlotType type;
	/**
	 * Slot's location relative to the island's origin
	 */
	private Vector3 location = new Vector3();
	private Quaternion rotation = new Quaternion();

	public Slot() {
	}

	public Slot(final Slot cloneFrom) {
		this(cloneFrom.type, cloneFrom.location, cloneFrom.rotation);
	}

	public Slot(final SlotType type, final Vector3 location, final Quaternion rotation) {
		this.type = type;
		this.location = location;
		this.rotation = rotation;
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

	public Quaternion getRotation() {
		return this.rotation;
	}

	public void setRotation(final Quaternion rotation) {
		this.rotation = rotation;
	}

	@Override
	public String toString() {
		return "Slot [type=" + this.type + ", location=" + this.location + ", rotation=" + this.rotation + "]";
	}
}
