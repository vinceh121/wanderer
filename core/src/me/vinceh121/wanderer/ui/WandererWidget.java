package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import me.vinceh121.wanderer.Wanderer;

public abstract class WandererWidget extends Widget {
	protected final Wanderer game;

	public WandererWidget(final Wanderer game) {
		this.game = game;
	}

	public Wanderer getGame() {
		return this.game;
	}
}
