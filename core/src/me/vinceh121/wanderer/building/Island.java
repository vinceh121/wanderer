package me.vinceh121.wanderer.building;

import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractLivingEntity;

public class Island extends AbstractLivingEntity implements IClanMember {
	private final Array<Slot> slots = new Array<>();
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
