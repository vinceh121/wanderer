package me.vinceh121.wanderer.artifact;

import com.badlogic.gdx.graphics.Color;

import me.vinceh121.wanderer.IMeta;

public abstract class ArtifactMeta implements IMeta {
	private String artifactModel, artifactTexture;
	private Color artifactColor = new Color(0f, 0.8f, 1f, 0f);
	private boolean rotate = true;
	private float pickupZoneRadius = 2f;

	public String getArtifactModel() {
		return this.artifactModel;
	}

	public void setArtifactModel(final String artifactModel) {
		this.artifactModel = artifactModel;
	}

	public String getArtifactTexture() {
		return this.artifactTexture;
	}

	public void setArtifactTexture(final String artifactTexture) {
		this.artifactTexture = artifactTexture;
	}

	public Color getArtifactColor() {
		return this.artifactColor;
	}

	public void setArtifactColor(final Color artifactColor) {
		this.artifactColor = artifactColor;
	}

	public boolean isRotate() {
		return this.rotate;
	}

	public void setRotate(final boolean rotate) {
		this.rotate = rotate;
	}

	public float getPickupZoneRadius() {
		return this.pickupZoneRadius;
	}

	public void setPickupZoneRadius(final float pickupZoneRadius) {
		this.pickupZoneRadius = pickupZoneRadius;
	}
}