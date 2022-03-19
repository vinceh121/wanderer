package me.vinceh121.wanderer.character.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;

public class DebugOverlay extends Table {
	private final Wanderer game;
	private final Label lblFps = new Label("FPS", this.getSkin()), lblEntities = new Label("Entities", this.getSkin());

	public DebugOverlay(final Wanderer game) {
		super(WandererConstants.getDevSkin());
		this.game = game;
		this.add(this.lblFps).align(Align.left);
		this.row();
		this.add(this.lblEntities).align(Align.left);
		this.align(Align.topLeft);
	}

	@Override
	public void act(final float delta) {
		this.lblFps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
		this.lblEntities.setText("Entities: " + this.game.getEntities().size);

		// pin to the top left
		this.setX(0, Align.topLeft);
		this.setY(this.getStage().getHeight(), Align.topLeft);

		super.act(delta);
	}
}
