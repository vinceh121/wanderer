package me.vinceh121.wanderer.artifact;

public abstract class ArtifactMeta {
	private int energyRequired;
	private boolean red;
	private String artifactModel, artifactTexture;

	public ArtifactMeta(int energyRequired, boolean red, String artifactModel, String artifactTexture) {
		this.energyRequired = energyRequired;
		this.red = red;
		this.artifactModel = artifactModel;
		this.artifactTexture = artifactTexture;
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

	public String getArtifactModel() {
		return artifactModel;
	}

	public void setArtifactModel(String artifactModel) {
		this.artifactModel = artifactModel;
	}

	public String getArtifactTexture() {
		return artifactTexture;
	}

	public void setArtifactTexture(String artifactTexture) {
		this.artifactTexture = artifactTexture;
	}
}
