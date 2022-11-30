package me.vinceh121.wanderer;

import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.clan.Clan;

public class MapW {
	private boolean loadPlayerIsland;
	private Array<ObjectNode> entities;
	private Array<Clan> clans;

	public boolean isLoadPlayerIsland() {
		return loadPlayerIsland;
	}

	public void setLoadPlayerIsland(boolean loadPlayerIsland) {
		this.loadPlayerIsland = loadPlayerIsland;
	}

	public Array<ObjectNode> getEntities() {
		return entities;
	}

	public void setEntities(Array<ObjectNode> entities) {
		this.entities = entities;
	}

	public Array<Clan> getClans() {
		return clans;
	}

	public void setClans(Array<Clan> clans) {
		this.clans = clans;
	}
}
