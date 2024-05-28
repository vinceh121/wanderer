package me.vinceh121.wanderer.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.ID;
import me.vinceh121.wanderer.Wanderer;

public class NavigationWaypoint extends AbstractEntity {
	private final List<ID> neighbors = new ArrayList<>();
	private boolean onlyOnce, enabled;
	private ID forcedNext;
	private String label;

	public NavigationWaypoint(final Wanderer game) {
		super(game);
	}

	@Override
	public void writeState(ObjectNode node) {
		super.writeState(node);

		node.put("enabled", this.enabled);
		node.put("onlyOnce", this.onlyOnce);
		node.putPOJO("forcedNext", this.forcedNext);
		node.put("label", this.label);

		final ArrayNode arrNei = node.putArray("neighbors");
		for (final ID mem : this.neighbors) {
			arrNei.add(mem.getValue());
		}
	}

	@Override
	public void readState(ObjectNode node) {
		super.readState(node);

		this.enabled = node.get("enabled").asBoolean();
		this.onlyOnce = node.get("onlyOnce").asBoolean();
		this.forcedNext = node.hasNonNull("forcedNext") ? new ID(node.get("forcedNext").asInt()) : null;
		this.label = node.get("label").asText();

		this.neighbors.clear();
		final ArrayNode arrNei = node.withArray("neighbors");
		for (final JsonNode nodes : arrNei) {
			this.neighbors.add(new ID(nodes.asInt()));
		}
	}

	public boolean isOnlyOnce() {
		return onlyOnce;
	}

	public void setOnlyOnce(boolean onlyOnce) {
		this.onlyOnce = onlyOnce;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public ID getForcedNext() {
		return forcedNext;
	}

	public void setForcedNext(ID forcedNext) {
		this.forcedNext = forcedNext;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<ID> getNeighbors() {
		return neighbors;
	}
}
