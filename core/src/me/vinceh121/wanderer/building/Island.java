package me.vinceh121.wanderer.building;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractLivingEntity;

public class Island extends AbstractLivingEntity implements IClanMember {
	private final Array<Slot> slots = new Array<>();
	private final Array<Building> buildings = new Array<>();
	private Clan clan;

	public Island(final Wanderer game) {
		super(game);
	}

	@Override
	public void onDeath() {
		// TODO
	}

	/**
	 * @param value
	 * @see com.badlogic.gdx.utils.Array#add(java.lang.Object)
	 */
	public void addSlot(final Slot value) {
		this.slots.add(value);
	}

	/**
	 * @param index
	 * @return
	 * @see com.badlogic.gdx.utils.Array#get(int)
	 */
	public Slot getSlot(final int index) {
		return this.slots.get(index);
	}

	public Array<Slot> getSlots() {
		return this.slots;
	}

	public void addBuilding(final Building value, final Slot slot) {
		if (this.isSlotTaken(slot)) {
			throw new IllegalStateException("Slot is taken");
		}
		buildings.add(value);
		value.setIsland(this);
		value.setSlot(slot);
		this.updateBuildings();
	}

	private void updateBuildings() {
		final Matrix4 islandTrans = this.getTransform();
		for (Building building : this.buildings) {
			building.setTransform(islandTrans.cpy().translate(building.getSlot().getLocation()));
		}
	}

	public boolean isSlotTaken(Slot slot) {
		for (Building b : this.buildings) {
			if (b.getSlot() == slot) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Clan getClan() {
		return this.clan;
	}

	@Override
	public void onJoinClan(final Clan clan) {
		this.clan = clan;
		// TODO probably nothing?
	}
}
