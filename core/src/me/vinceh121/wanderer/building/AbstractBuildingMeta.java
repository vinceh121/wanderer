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

	public AbstractBuildingMeta() {
	}

	public AbstractBuildingMeta(int energyRequired, boolean red, String artifactModel, String artifactTexture) {
		this.energyRequired = energyRequired;
		this.setRed(red);
		this.setArtifactModel(artifactModel);
		this.setArtifactTexture(artifactTexture);
	}

	public Array<DisplayModel> getDisplayModels() {
		return displayModels;
	}

	public void setDisplayModels(Array<DisplayModel> displayModels) {
		this.displayModels = displayModels;
	}
	
	public void addModel(DisplayModel value) {
		this.displayModels.add(value);
	}

	public boolean removeModel(DisplayModel value) {
		return this.displayModels.removeValue(value, true);
	}

	public DisplayModel removeModel(int index) {
		return this.displayModels.removeIndex(index);
	}

	public String getCollisionModel() {
		return collisionModel;
	}

	public void setCollisionModel(String collisionModel) {
		this.collisionModel = collisionModel;
	}

	public int getEnergyRequired() {
		return energyRequired;
	}

	public void setEnergyRequired(int energyRequired) {
		this.energyRequired = energyRequired;
	}

	/**
	 * @return Build time in seconds
	 */
	public int getBuildTime() {
		return buildTime;
	}

	/**
	 * @param buildTime Build time in seconds
	 */
	public void setBuildTime(int buildTime) {
		this.buildTime = buildTime;
	}

	public boolean isRed() {
		return red;
	}

	/**
	 * Automatically sets color
	 * 
	 * @param red
	 */
	public void setRed(boolean red) {
		this.red = red;
		if (red) {
			this.setArtifactColor(new Color(1f, 0.1f, 0f, 0f));
		} else {
			this.setArtifactColor(new Color(0f, 0.8f, 1f, 0f));
		}
	}

	public boolean isShrink() {
		return shrink;
	}

	public void setShrink(boolean shrink) {
		this.shrink = shrink;
	}

	public float getInteractZoneRadius() {
		return interactZoneRadius;
	}

	public void setInteractZoneRadius(float interactZoneRadius) {
		this.interactZoneRadius = interactZoneRadius;
	}

	public float getInteractZoneHeight() {
		return interactZoneHeight;
	}

	public void setInteractZoneHeight(float interactZoneHeight) {
		this.interactZoneHeight = interactZoneHeight;
	}

	public abstract AbstractBuilding createBuilding(Wanderer game);
}