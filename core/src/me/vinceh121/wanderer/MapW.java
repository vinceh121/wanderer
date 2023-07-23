package me.vinceh121.wanderer;

import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.clan.Clan;

public class MapW {
	private Array<ObjectNode> entities;
	private Array<Clan> clans;

	public Array<ObjectNode> getEntities() {
		return this.entities;
	}

	public void setEntities(final Array<ObjectNode> entities) {
		this.entities = entities;
	}

	public Array<Clan> getClans() {
		return this.clans;
	}

	public void setClans(final Array<Clan> clans) {
		this.clans = clans;
	}
}
