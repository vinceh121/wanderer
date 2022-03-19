package me.vinceh121.wanderer.building;

import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractLivingEntity;

public class Island extends AbstractLivingEntity implements IClanMember {
	private final Array<Slot> slots = new Array<>();
	private final Clan clan;

	public Island(Wanderer game, Clan clan) {
		super(game);
		this.clan = clan;
	}

	@Override
	public void onDeath() {
		// TODO
	}

	/**
	 * @param value
	 * @see com.badlogic.gdx.utils.Array#add(java.lang.Object)
	 */
	public void addSlot(Slot value) {
		slots.add(value);
	}

	/**
	 * @param index
	 * @return
	 * @see com.badlogic.gdx.utils.Array#get(int)
	 */
	public Slot getSlot(int index) {
		return slots.get(index);
	}

	public Array<Slot> getSlots() {
		return slots;
	}

	@Override
	public Clan getClan() {
		return this.clan;
	}

	@Override
	public void onJoinClan(Clan clan) {
		// TODO probably nothing?
	}
}
