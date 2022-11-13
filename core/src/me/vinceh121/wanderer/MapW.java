package me.vinceh121.wanderer;

import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class MapW {
	private boolean loadPlayerIsland;
	private Array<AbstractEntity> entities;
	private Array<Clan> clans;

	public boolean isLoadPlayerIsland() {
		return loadPlayerIsland;
	}

	public void setLoadPlayerIsland(boolean loadPlayerIsland) {
		this.loadPlayerIsland = loadPlayerIsland;
	}

	public Array<AbstractEntity> getEntities() {
		return entities;
	}

	public void setEntities(Array<AbstractEntity> entities) {
		this.entities = entities;
	}

	public Array<Clan> getClans() {
		return clans;
	}

	public void setClans(Array<Clan> clans) {
		this.clans = clans;
	}
}
