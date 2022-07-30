package me.vinceh121.wanderer.building;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.artifact.ArtifactMeta;
import me.vinceh121.wanderer.entity.DisplayModel;

public abstract class AbstractBuildingMeta extends ArtifactMeta {
	private Array<DisplayModel> displayModels = new Array<>();
	private String collisionModel;
	private int energyRequired, buildTime;
	private boolean red, shrink = true;
	private float interactZoneRadius = 11f, interactZoneHeight = 20f;
	private SlotType slotType = SlotType.GENERIC;

	public AbstractBuildingMeta() {
	}

	public AbstractBuildingMeta(final int energyRequired, final boolean red, final String artifactModel,
			final String artifactTexture) {
		this.energyRequired = energyRequired;
		this.setRed(red);
		this.setArtifactModel(artifactModel);
		this.setArtifactTexture(artifactTexture);
	}

	public Array<DisplayModel> getDisplayModels() {
		return this.displayModels;
	}

	public void setDisplayModels(final Array<DisplayModel> displayModels) {
		this.displayModels = displayModels;
	}

	public void addModel(final DisplayModel value) {
		this.displayModels.add(value);
	}

	public boolean removeModel(final DisplayModel value) {
		return this.displayModels.removeValue(value, true);
	}

	public DisplayModel removeModel(final int index) {
		return this.displayModels.removeIndex(index);
	}

	public String getCollisionModel() {
		return this.collisionModel;
	}

	public void setCollisionModel(final String collisionModel) {
		this.collisionModel = collisionModel;
	}

	public int getEnergyRequired() {
		return this.energyRequired;
	}

	public void setEnergyRequired(final int energyRequired) {
		this.energyRequired = energyRequired;
	}

	/**
	 * @return Build time in seconds
	 */
	public int getBuildTime() {
		return this.buildTime;
	}

	/**
	 * @param buildTime Build time in seconds
	 */
	public void setBuildTime(final int buildTime) {
		this.buildTime = buildTime;
	}

	public boolean isRed() {
		return this.red;
	}

	/**
	 * Automatically sets color
	 *
	 * @param red
	 */
	public void setRed(final boolean red) {
		this.red = red;
		if (red) {
			this.setArtifactColor(new Color(1f, 0.1f, 0f, 0f));
		} else {
			this.setArtifactColor(new Color(0f, 0.8f, 1f, 0f));
		}
	}

	public boolean isShrink() {
		return this.shrink;
	}

	public void setShrink(final boolean shrink) {
		this.shrink = shrink;
	}

	public float getInteractZoneRadius() {
		return this.interactZoneRadius;
	}

	public void setInteractZoneRadius(final float interactZoneRadius) {
		this.interactZoneRadius = interactZoneRadius;
	}

	public float getInteractZoneHeight() {
		return this.interactZoneHeight;
	}

	public void setInteractZoneHeight(final float interactZoneHeight) {
		this.interactZoneHeight = interactZoneHeight;
	}

	public SlotType getSlotType() {
		return this.slotType;
	}

	public void setSlotType(final SlotType slotType) {
		this.slotType = slotType;
	}

	public abstract AbstractBuilding createBuilding(Wanderer game);
}