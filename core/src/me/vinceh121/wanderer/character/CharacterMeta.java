package me.vinceh121.wanderer.character;

import java.util.Arrays;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.IMeta;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.platform.audio.Sound3D;

public class CharacterMeta implements IMeta {
	private String name, model, texture, fallSound;
	private String[] steps, stepsSide;
	private boolean storyMode;
	private Vector3 capsuleOffset = new Vector3(0, 1.2699996f, 0);
	private float capsuleRadius = 0.5f, capsuleHeight = 1.5f;

	/**
	 * @return the model
	 */
	public String getModel() {
		return this.model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(final String model) {
		this.model = model;
	}

	/**
	 * @return the texture
	 */
	public String getTexture() {
		return this.texture;
	}

	/**
	 * @param texture the texture to set
	 */
	public void setTexture(final String texture) {
		this.texture = texture;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the fallSound
	 */
	public String getFallSound() {
		return this.fallSound;
	}

	/**
	 * @param fallSound the fallSound to set
	 */
	public void setFallSound(final String fallSound) {
		this.fallSound = fallSound;
	}

	/**
	 * @return the steps
	 */
	public String[] getSteps() {
		return this.steps;
	}

	/**
	 * @param steps the steps to set
	 */
	public void setSteps(final String[] steps) {
		this.steps = steps;
	}

	/**
	 * @return the stepsSide
	 */
	public String[] getStepsSide() {
		return this.stepsSide;
	}

	/**
	 * @param stepsSide the stepsSide to set
	 */
	public void setStepsSide(final String[] stepsSide) {
		this.stepsSide = stepsSide;
	}

	/**
	 * @return the storyMode
	 */
	public boolean isStoryMode() {
		return this.storyMode;
	}

	/**
	 * @param storyMode the storyMode to set
	 */
	public void setStoryMode(final boolean storyMode) {
		this.storyMode = storyMode;
	}

	public Vector3 getCapsuleOffset() {
		return this.capsuleOffset;
	}

	public void setCapsuleOffset(final Vector3 capsuleOffset) {
		this.capsuleOffset = capsuleOffset;
	}

	public float getCapsuleRadius() {
		return this.capsuleRadius;
	}

	public void setCapsuleRadius(final float capsuleRadius) {
		this.capsuleRadius = capsuleRadius;
	}

	public float getCapsuleHeight() {
		return this.capsuleHeight;
	}

	public void setCapsuleHeight(final float capsuleHeight) {
		this.capsuleHeight = capsuleHeight;
	}

	/**
	 * Asynchronously loads the assets contained by this {@link CharacterMeta}
	 */
	public void ensureLoading() {
		WandererConstants.ASSET_MANAGER.load(this.model, Model.class);
		WandererConstants.ASSET_MANAGER.load(this.texture, Texture.class, WandererConstants.MIPMAPS);
		WandererConstants.ASSET_MANAGER.load(this.fallSound, Sound3D.class);

		for (final String s : this.steps) {
			WandererConstants.ASSET_MANAGER.load(s, Sound3D.class);
		}

		for (final String s : this.stepsSide) {
			WandererConstants.ASSET_MANAGER.load(s, Sound3D.class);
		}
	}

	@Override
	public AbstractEntity create(final Wanderer game) {
		return new CharacterW(game, this);
	}

	@Override
	public String toString() {
		return "CharacterMeta [name=" + this.name + ", model=" + this.model + ", texture=" + this.texture
				+ ", fallSound=" + this.fallSound + ", steps=" + Arrays.toString(this.steps) + ", stepsSide="
				+ Arrays.toString(this.stepsSide) + ", storyMode=" + this.storyMode + "]";
	}
}
