package me.vinceh121.wanderer;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;

import me.vinceh121.wanderer.entity.AbstractEntity;

public interface IPrototype {
	/**
	 * Returns a new instance of the concerned entity prototype. Perhaps a default
	 * if this prototype can be used on multiple entities. This method should not
	 * add the entity to the game.
	 *
	 * @param game An instance of Wanderer
	 * @return A new instance of the entity
	 */
	AbstractEntity create(Wanderer game);
	
	void getAssetsToLoad(List<AssetDescriptor<?>> descriptors);
}
