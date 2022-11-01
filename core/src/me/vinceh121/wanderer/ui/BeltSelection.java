package me.vinceh121.wanderer.ui;

import java.util.Objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.animation.TransformAnimation;
import me.vinceh121.wanderer.artifact.ArtifactMeta;
import me.vinceh121.wanderer.entity.DisplayModel;

public class BeltSelection extends WandererWidget {
	private final Array<ArtifactMeta> belt;
	private final Quaternion rotation = new Quaternion();
	private final Matrix4[] previousTransforms;
	private float angle;
	private int index;

	public BeltSelection(final Wanderer game, final Array<ArtifactMeta> belt) {
		super(game);
		Objects.nonNull(belt);
		this.belt = belt;
		this.previousTransforms = new Matrix4[this.belt.size];
		for (int i = 0; i < this.previousTransforms.length; i++) {
			this.previousTransforms[i] = new Matrix4();
		}
	}

	@Override
	public void draw(final Batch batch, final float parentAlpha) {
		super.draw(batch, parentAlpha);

		if (this.belt.size == 0) {
			return;
		}

		this.angle += 3f;
		this.rotation.setFromAxis(Vector3.Y, this.angle);

		// left hand side
		final Vector3 scale = new Vector3(1.5f, 1.5f, 1.5f);
		for (int i = this.index - 1; i >= 0; i--) {
			final ArtifactMeta artifact = this.belt.get(i);

			final DisplayModel m = new DisplayModel(artifact.getArtifactModel(), artifact.getArtifactTexture());

			final int artefactWidth = this.getModelWidthOrDefault(m);

			final Matrix4 trans = new Matrix4(
					new Vector3(this.getWidth() / 2 - artefactWidth / 2 - artefactWidth * (i + 1),
							this.getHeight() / 2,
							-32),
					this.rotation,
					scale);

			this.setModelTransform(i, trans, m);

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

			final int artefactWidth = this.getModelWidthOrDefault(m);

			final Matrix4 trans = new Matrix4(
					new Vector3(this.getWidth() / 2 - artefactWidth / 2, this.getHeight() / 2, -32),
					this.rotation,
					new Vector3(2.5f, 2.5f, 2.5f));

			this.setModelTransform(this.index, trans, m);

			m.addTextureAttribute(ColorAttribute.createEmissive(artifact.getArtifactColor()));
			m.render(this.game.getGraphicsManager().getModelBatch(), this.game.getGraphicsManager().getEnv());
		}

		// right hand side
		scale.set(2, 2, 2);
		for (int i = this.index + 1; i < this.belt.size; i++) {
			final ArtifactMeta artifact = this.belt.get(i);

			final DisplayModel m = new DisplayModel(artifact.getArtifactModel(), artifact.getArtifactTexture());

			final int artefactWidth = this.getModelWidthOrDefault(m);

			final Matrix4 trans = new Matrix4(
					new Vector3(this.getWidth() / 2 + artefactWidth / 2 + artefactWidth * (i - this.index - 1),
							this.getHeight() / 2,
							-32),
					this.rotation,
					scale);

			this.setModelTransform(i, trans, m);

			m.addTextureAttribute(ColorAttribute.createEmissive(artifact.getArtifactColor()));
			m.render(this.game.getGraphicsManager().getModelBatch(), this.game.getGraphicsManager().getEnv());
			if (scale.x > 1.5) {
				scale.scl(0.9f);
			}
		}
	}

	private void setModelTransform(int i, Matrix4 trans, DisplayModel m) {
		m.setAbsoluteTransform(
				TransformAnimation.interpolate(trans, this.previousTransforms[i], 0.8f, Interpolation.sine));
		this.previousTransforms[i].set(m.getAbsoluteTransform());
	}

	private int getModelWidthOrDefault(final DisplayModel m) {
		if (m.getCacheDisplayModel() == null) {
			return 42;
		}
		final BoundingBox bb = new BoundingBox();
		m.getCacheDisplayModel().calculateBoundingBox(bb);
		return (int) bb.getWidth();
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
