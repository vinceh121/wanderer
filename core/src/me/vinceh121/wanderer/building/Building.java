package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractLivingEntity;

public class Building extends AbstractLivingEntity implements IClanMember {
	private Clan clan;
	private Island island;
	private Slot slot;

	public Building(Wanderer game) {
		super(game);
	}

	/**
	 * @return the island
	 */
	public Island getIsland() {
		return island;
	}

	/**
	 * @param island the island to set
	 */
	public void setIsland(Island island) {
		this.island = island;
	}

	/**
	 * @return the slot
	 */
	public Slot getSlot() {
		return slot;
	}

	/**
	 * @param slot the slot to set
	 */
	public void setSlot(Slot slot) {
		this.slot = slot;
	}

	@Override
	public void onDeath() {
		// TODO explode
	}

	@Override
	public Clan getClan() {
		return this.clan;
	}

	@Override
	public void onJoinClan(Clan clan) {
		this.clan = clan;
		// TODO change light decal's color
	}
}
