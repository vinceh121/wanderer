package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractLivingEntity;

public class Building extends AbstractLivingEntity implements IClanMember {
	private Clan clan;
	private Island island;
	private Slot slot;

	public Building(final Wanderer game) {
		super(game);
	}

	/**
	 * @return the island
	 */
	public Island getIsland() {
		return this.island;
	}

	/**
	 * @param island the island to set
	 */
	public void setIsland(final Island island) {
		this.island = island;
	}

	/**
	 * @return the slot
	 */
	public Slot getSlot() {
		return this.slot;
	}

	/**
	 * @param slot the slot to set
	 */
	public void setSlot(final Slot slot) {
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
	public void onJoinClan(final Clan clan) {
		this.clan = clan;
		// TODO change light decal's color
	}
}
