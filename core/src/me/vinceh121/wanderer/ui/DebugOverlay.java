package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.core.JsonProcessingException;

import me.vinceh121.wanderer.StoryWanderer;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class DebugOverlay extends Table {
	private final Wanderer game;
	private final Label lblGpuInfo = new Label("Gpu Info", this.getSkin()), lblFps = new Label("FPS", this.getSkin()),
			lblEntities = new Label("Entities", this.getSkin()), lblCoords = new Label("Coords", this.getSkin()),
			lblTime = new Label("Time", this.getSkin());
	private final Label txtPartState = new Label("Part state", this.getSkin());

	public DebugOverlay(final Wanderer game) {
		super(WandererConstants.getDevSkin());
		this.game = game;
		this.add(this.lblGpuInfo).align(Align.left);
		this.row();
		this.add(this.lblFps).align(Align.left);
		this.row();
		this.add(this.lblEntities).align(Align.left);
		this.row();
		this.add(this.lblCoords).align(Align.left);
		this.row();
		this.add(this.lblTime).align(Align.left);
		this.row();
		this.add(this.txtPartState).align(Align.left);
		this.align(Align.topLeft);
	}

	@Override
	public void act(final float delta) {
		this.lblGpuInfo.setText(Gdx.graphics.getGLVersion().getDebugVersionString());
		this.lblFps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
		this.lblEntities.setText("Entities: " + this.game.getEntities().size);

		if (this.game.getControlledEntity() instanceof AbstractEntity) {// doubles as null-check
			this.lblCoords.setText("Coords (controlled): "
					+ ((AbstractEntity) this.game.getControlledEntity()).getTransform().getTranslation(new Vector3())
					+ "\nCoords (camera): " + this.game.getCamera().position);
		} else {
			this.lblCoords.setText("Coords (camera): " + this.game.getCamera().position);
		}

		this.lblTime.setText(String.format("Time: %.2f%%", this.game.getTimeOfDay() * 100));

		if (this.game instanceof StoryWanderer) {
			try {
				this.txtPartState.setText("Part state: " + WandererConstants.MAPPER
					.writeValueAsString(((StoryWanderer) this.game).getPart().getState()));
			} catch (final JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		} else {
			this.txtPartState.setText("Part state: not in story mode");
		}

		// pin to the top left
		this.setX(0, Align.topLeft);
		this.setY(this.getStage().getHeight(), Align.topLeft);

		super.act(delta);
	}
}
