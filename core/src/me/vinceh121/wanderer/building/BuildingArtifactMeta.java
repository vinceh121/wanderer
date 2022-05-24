package me.vinceh121.wanderer.building;

import com.badlogic.gdx.graphics.Color;

import me.vinceh121.wanderer.artifact.ArtifactMeta;

public class BuildingArtifactMeta extends ArtifactMeta {
	private int energyRequired;
	private boolean red, shrink = true;

	public BuildingArtifactMeta() {
	}

	public BuildingArtifactMeta(int energyRequired, boolean red, String artifactModel, String artifactTexture) {
		this.energyRequired = energyRequired;
		this.setRed(red);
		this.setArtifactModel(artifactModel);
		this.setArtifactTexture(artifactTexture);
	}

	public int getEnergyRequired() {
		return energyRequired;
	}

	public void setEnergyRequired(int energyRequired) {
		this.energyRequired = energyRequired;
	}

	public boolean isRed() {
		return red;
	}

	/**
	 * Automatically sets color
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
}