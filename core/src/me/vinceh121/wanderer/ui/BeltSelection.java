package me.vinceh121.wanderer.ui;

import java.util.Objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.artifact.ArtifactMeta;
import me.vinceh121.wanderer.entity.DisplayModel;

public class BeltSelection extends WandererWidget {
	private final Array<ArtifactMeta> belt;
	private final Quaternion rotation = new Quaternion();
	private float angle;
	private int index;

	public BeltSelection(final Wanderer game, final Array<ArtifactMeta> belt) {
		super(game);
		Objects.nonNull(belt);
		this.belt = belt;
	}

	@Override
	public void draw(final Batch batch, final float parentAlpha) {
		super.draw(batch, parentAlpha);

		if (this.belt.size == 0) {
			return;
		}

		this.angle += 3f;
		this.rotation.setFromAxis(Vector3.Y, this.angle);

		final int artefactWidth = 42;

		// left hand side
		final Vector3 scale = new Vector3(1.5f, 1.5f, 1.5f);
		for (int i = this.index; i >= 0; i--) {
			final ArtifactMeta artifact = this.belt.get(i);

			final DisplayModel m = new DisplayModel(artifact.getArtifactModel(), artifact.getArtifactTexture());
			m.setAbsoluteTransform(new Matrix4(
					new Vector3(this.getWidth() / 2 - artefactWidth / 2 - artefactWidth * i, this.getHeight() / 2, -32),
					this.rotation,
					scale));

			m.addTextureAttribute(ColorAttribute.createEmissive(artifact.getArtifactColor()));
			m.render(this.game.getGraphicsManager().getModelBatch(), this.game.getGraphicsManager().getEnv());
			if (scale.x < 2) {
				scale.scl(1.1f);
			}
		}

		// selected artifact
		{
			final ArtifactMeta artifact = this.belt.get(this.index);

			final DisplayModel m = new DisplayModel(artifact.getArtifactModel(), artifact.getArtifactTexture());
			m.setAbsoluteTransform(
					new Matrix4(new Vector3(this.getWidth() / 2 - artefactWidth / 2, this.getHeight() / 2, -32),
							this.rotation,
							new Vector3(2.5f, 2.5f, 2.5f)));

			m.addTextureAttribute(ColorAttribute.createEmissive(artifact.getArtifactColor()));
			m.render(this.game.getGraphicsManager().getModelBatch(), this.game.getGraphicsManager().getEnv());
		}

		// right hand side
		scale.set(2, 2, 2);
		for (int i = this.index + 1; i < this.belt.size; i++) {
			final ArtifactMeta artifact = this.belt.get(i);

			final DisplayModel m = new DisplayModel(artifact.getArtifactModel(), artifact.getArtifactTexture());
			m.setAbsoluteTransform(
					new Matrix4(
							new Vector3(this.getWidth() / 2 + artefactWidth / 2 + artefactWidth * (i - this.index - 1),
									this.getHeight() / 2,
									-32),
							this.rotation,
							scale));

			m.addTextureAttribute(ColorAttribute.createEmissive(artifact.getArtifactColor()));
			m.render(this.game.getGraphicsManager().getModelBatch(), this.game.getGraphicsManager().getEnv());
			if (scale.x > 1.5) {
				scale.scl(0.9f);
			}
		}
	}

	public void increment() {
		this.index = Math.min(this.index + 1, this.belt.size - 1);
		this.game.showMessage(this.belt.get(this.index).toString());
	}

	public void decrement() {
		this.index = Math.max(this.index - 1, 0);
		this.game.showMessage(this.belt.get(this.index).toString());
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}
}
