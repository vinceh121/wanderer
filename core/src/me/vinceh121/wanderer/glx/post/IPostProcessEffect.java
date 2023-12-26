package me.vinceh121.wanderer.glx.post;

import com.badlogic.gdx.graphics.Texture;

import me.vinceh121.wanderer.GraphicsManager;

public interface IPostProcessEffect {
	Texture process(GraphicsManager glx, Texture tex);

	default boolean isInPlace() {
		return true;
	}
}
