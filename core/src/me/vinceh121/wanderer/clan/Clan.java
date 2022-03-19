package me.vinceh121.wanderer.clan;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class Clan {
	private String name;
	private Color color = Color.WHITE;
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
