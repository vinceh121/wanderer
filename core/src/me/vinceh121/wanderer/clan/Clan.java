package me.vinceh121.wanderer.clan;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.ID;
import me.vinceh121.wanderer.ISaveable;
import me.vinceh121.wanderer.WandererConstants;

public class Clan implements ISaveable {
	private ID id = new ID();
	private String name;
	private Color color = Color.WHITE;
	private int energy, maxEnergy;
	/**
	 * Contains player, buildings and island ids
	 */
	private final Array<ID> members = new Array<>();
	private final Map<Clan, Amicability> relationships = new HashMap<>();
	private Amicability defaultAmicability = Amicability.NEUTRAL;

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

	public Array<ID> getMembers() {
		return this.members;
	}

	public void addMember(final IClanMember value) {
		this.members.add(value.getId());
		value.onJoinClan(this);
	}

	public boolean removeMember(final IClanMember value) {
		return this.members.removeValue(value.getId(), true); // compare equality, not pointers
	}

	public boolean removeMember(final ID value) {
		return this.members.removeValue(value, true); // compare equality, not pointers
	}

	public Map<Clan, Amicability> getRelationships() {
		return this.relationships;
	}

	public void putRelationship(Clan clan, Amicability amicability) {
		this.relationships.put(clan, amicability);
	}

	public Amicability getRelationship(Clan clan) {
		Amicability amicability = this.relationships.get(clan);

		if (amicability == null) {
			return this.defaultAmicability;
		}

		return amicability;
	}

	public Amicability getDefaultAmicability() {
		return this.defaultAmicability;
	}

	public void setDefaultAmicability(Amicability defaultAmicability) {
		this.defaultAmicability = defaultAmicability;
	}

	@Override
	public void writeState(final ObjectNode node) {
		node.put("id", this.getId().getValue());
		node.put("name", this.getName());
		node.putPOJO("color", this.getColor());
		node.put("energy", this.getEnergy());
		node.put("maxEnergy", this.getMaxEnergy());

		final ArrayNode arrMem = node.putArray("members");
		for (final ID mem : this.getMembers()) {
			arrMem.add(mem.getValue());
		}
	}

	@Override
	public void readState(final ObjectNode node) {
		this.id = new ID(node.get("id").asInt());
		this.setName(node.get("name").asText());
		this.setColor(WandererConstants.MAPPER.convertValue(node.get("color"), Color.class));
		this.setEnergy(node.get("energy").asInt());
		this.setMaxEnergy(node.get("maxEnergy").asInt());

		this.members.clear();
		final ArrayNode arrMem = node.withArray("members");
		for (final JsonNode nodes : arrMem) {
			// FIXME need to call onJoinClan here
			this.members.add(new ID(nodes.asInt()));
		}
	}
}
