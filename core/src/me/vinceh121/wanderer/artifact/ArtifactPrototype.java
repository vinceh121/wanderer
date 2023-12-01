package me.vinceh121.wanderer.artifact;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

import me.vinceh121.wanderer.IPrototype;

public abstract class ArtifactPrototype implements IPrototype {
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

	@Override
	public void getAssetsToLoad(final List<AssetDescriptor<?>> descriptors) {
		descriptors.add(new AssetDescriptor<>(this.artifactModel, Model.class));
		descriptors.add(new AssetDescriptor<>(this.artifactTexture, Texture.class));
	}
}