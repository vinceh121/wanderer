package me.vinceh121.wanderer.clan;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class Clan {
	private String name;
	private Color color = Color.WHITE;
	/**
	 * Contains player, buildings and island
	 */
	private Array<IClanMember> members = new Array<>();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the members
	 */
	public Array<IClanMember> getMembers() {
		return members;
	}

	public void addMember(IClanMember value) {
		members.add(value);
		value.onJoinClan(this);
	}

	/**
	 * @param value
	 * @param identity
	 * @return
	 * @see com.badlogic.gdx.utils.Array#removeValue(java.lang.Object, boolean)
	 */
	public boolean removeMember(IClanMember value) {
		return members.removeValue(value, true); // compare pointers, not equality
	}
}
