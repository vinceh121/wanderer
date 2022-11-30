package me.vinceh121.wanderer;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ISaveable {
	/**
	 * Writes this entity's state to the JSON object
	 *
	 * @param node JSON object to write the state to
	 */
	void writeState(ObjectNode node);

	/**
	 * Read this entity's state from the JSON object
	 *
	 * @param node JSON object to read data from
	 */
	void readState(ObjectNode node);
}
