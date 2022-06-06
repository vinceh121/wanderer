package me.vinceh121.wanderer.building;

import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.IMeta;

public class IslandMeta implements IMeta {
	private Array<Slot> slots = new Array<>();

	public IslandMeta() {
	}

	public IslandMeta(final Array<Slot> slots) {
		this.slots = slots;
	}

	public IslandMeta(final Slot... slots) {
		this.slots = new Array<>(slots);
	}

	public Array<Slot> getSlots() {
		return this.slots;
	}

	public void setSlots(final Array<Slot> slots) {
		this.slots = slots;
	}
}
