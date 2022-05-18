package me.vinceh121.wanderer.artifact;

import com.badlogic.gdx.graphics.Color;

public class ArtifactMeta {
	private String artifactModel, artifactTexture;
	private Color artifactColor = new Color(0f, 0.8f, 1f, 0f);
	private boolean rotate = true;
	private float interactZoneRadius = 2f;

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

	public Color getArtifactColor() {
		return artifactColor;
	}

	public void setArtifactColor(Color artifactColor) {
		this.artifactColor = artifactColor;
	}

	public boolean isRotate() {
		return rotate;
	}

	public void setRotate(boolean rotate) {
		this.rotate = rotate;
	}

	public float getInteractZoneRadius() {
		return interactZoneRadius;
	}

	public void setInteractZoneRadius(float interactZoneRadius) {
		this.interactZoneRadius = interactZoneRadius;
	}
}