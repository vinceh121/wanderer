package me.vinceh121.wanderer.building;

import com.badlogic.gdx.utils.Array;

public class IslandMeta {
	private Array<Slot> slots = new Array<>();

	public IslandMeta() {
	}

	public IslandMeta(Array<Slot> slots) {
		this.slots = slots;
	}

	public IslandMeta(Slot... slots) {
		this.slots = new Array<>(slots);
	}

	public Array<Slot> getSlots() {
		return slots;
	}

	public void setSlots(Array<Slot> slots) {
		this.slots = slots;
	}
}
