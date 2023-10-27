package me.vinceh121.wanderer;

import me.vinceh121.wanderer.entity.AbstractEntity;

/**
 * TODO rename to prototype, that's more semantically correct
 */
public interface IMeta {
	/**
	 * Returns a new instance of the concerned entity meta. Perhaps a default if
	 * this meta can be used on multiple entities. This method should not add the
	 * entity to the game.
	 *
	 * @param game An instance of Wanderer
	 * @return A new instance of the entity
	 */
	AbstractEntity create(Wanderer game);
}
