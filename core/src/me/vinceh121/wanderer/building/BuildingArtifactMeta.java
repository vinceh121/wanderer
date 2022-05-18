package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.artifact.ArtifactMeta;

public class BuildingArtifactMeta extends ArtifactMeta {
	private int energyRequired;
	private boolean red, shrink = true;

	public BuildingArtifactMeta() {
	}

	public BuildingArtifactMeta(int energyRequired, boolean red, String artifactModel, String artifactTexture) {
		this.energyRequired = energyRequired;
		this.red = red;
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

	public void setRed(boolean red) {
		this.red = red;
	}

	public boolean isShrink() {
		return shrink;
	}

	public void setShrink(boolean shrink) {
		this.shrink = shrink;
	}
}