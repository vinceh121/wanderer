package me.vinceh121.wanderer.clan;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class Clan {
	private String name;
	private Color color = Color.WHITE;
	private int energy, maxEnergy;
	/**
	 * Contains player, buildings and island
	 */
	private final Array<IClanMember> members = new Array<>();

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
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public int getMaxEnergy() {
		return maxEnergy;
	}

	public void setMaxEnergy(int maxEnergy) {
		this.maxEnergy = maxEnergy;
	}

	/**
	 * @return the members
	 */
	public Array<IClanMember> getMembers() {
		return this.members;
	}

	public void addMember(final IClanMember value) {
		this.members.add(value);
		value.onJoinClan(this);
	}

	/**
	 * @param value
	 * @param identity
	 * @return
	 * @see com.badlogic.gdx.utils.Array#removeValue(java.lang.Object, boolean)
	 */
	public boolean removeMember(final IClanMember value) {
		return this.members.removeValue(value, true); // compare pointers, not equality
	}
}
