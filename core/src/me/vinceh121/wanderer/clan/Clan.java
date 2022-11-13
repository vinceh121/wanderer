package me.vinceh121.wanderer.clan;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.ID;
import me.vinceh121.wanderer.Wanderer;

public class Clan {
	protected final Wanderer game;
	private final ID id = new ID();
	private String name;
	private Color color = Color.WHITE;
	private int energy, maxEnergy;
	/**
	 * Contains player, buildings and island
	 */
	private final Array<ID> memberIds = new Array<>();

	public Clan(Wanderer game) {
		this.game = game;
	}

	public ID getId() {
		return this.id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(final Color color) {
		this.color = color;
	}

	public int getEnergy() {
		return this.energy;
	}

	public void setEnergy(final int energy) {
		this.energy = energy;
	}

	public int getMaxEnergy() {
		return this.maxEnergy;
	}

	public void setMaxEnergy(final int maxEnergy) {
		this.maxEnergy = maxEnergy;
	}

	public Array<ID> getMembersIds() {
		return memberIds;
	}

	/**
	 * @return the members
	 */
	public Array<IClanMember> getMembers() {
		final Array<IClanMember> members = new Array<>(this.memberIds.size);
		for (final ID mId : this.memberIds) {
			members.add((IClanMember) this.game.getEntity(mId));
		}
		return members;
	}

	public void addMember(final IClanMember value) {
		this.memberIds.add(value.getId());
		value.onJoinClan(this);
	}

	/**
	 * @param value
	 * @param identity
	 * @return
	 * @see com.badlogic.gdx.utils.Array#removeValue(java.lang.Object, boolean)
	 */
	public boolean removeMember(final IClanMember value) {
		return this.memberIds.removeValue(value.getId(), false); // compare equality, not pointers
	}
}
