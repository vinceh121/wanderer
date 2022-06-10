package me.vinceh121.wanderer.building;

import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.IMeta;
import me.vinceh121.wanderer.entity.DisplayModel;

public class IslandMeta implements IMeta {
	private Array<DisplayModel> displayModels = new Array<>();
	private String collisionModel;
	private Array<Slot> slots = new Array<>();

	public IslandMeta() {
	}

	public IslandMeta(final Array<Slot> slots) {
		this.slots = slots;
	}

	public IslandMeta(final Slot... slots) {
		this.slots = new Array<>(slots);
	}

	public void addModel(final DisplayModel value) {
		this.displayModels.add(value);
	}

	public boolean removeModel(final DisplayModel value) {
		return this.displayModels.removeValue(value, true);
	}

	public DisplayModel removeModel(final int index) {
		return this.displayModels.removeIndex(index);
	}

	public Array<DisplayModel> getDisplayModels() {
		return displayModels;
	}

	public void setDisplayModels(Array<DisplayModel> displayModels) {
		this.displayModels = displayModels;
	}

	public String getCollisionModel() {
		return collisionModel;
	}

	public void setCollisionModel(String collisionModel) {
		this.collisionModel = collisionModel;
	}

	public Array<Slot> getSlots() {
		return this.slots;
	}

	public void setSlots(final Array<Slot> slots) {
		this.slots = slots;
	}
}
