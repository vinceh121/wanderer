package me.vinceh121.wanderer.entity;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

import me.vinceh121.wanderer.IPrototype;
import me.vinceh121.wanderer.Wanderer;

public class PropPrototype implements IPrototype {
	private String displayModel, collideModel, texture, detailMapTexture;
	private float mass;

	@Override
	public void getAssetsToLoad(List<AssetDescriptor<?>> descriptors) {
		descriptors.add(new AssetDescriptor<>(this.displayModel, Model.class));
		descriptors.add(new AssetDescriptor<>(this.texture, Texture.class));

		if (this.collideModel != null) {
			descriptors.add(new AssetDescriptor<>(this.collideModel, Model.class));
		}

		if (this.detailMapTexture != null) {
			descriptors.add(new AssetDescriptor<>(this.detailMapTexture, Texture.class));
		}
	}

	public String getDisplayModel() {
		return this.displayModel;
	}

	public void setDisplayModel(final String displayModel) {
		this.displayModel = displayModel;
	}

	public String getCollideModel() {
		return this.collideModel;
	}

	public void setCollideModel(final String collideModel) {
		this.collideModel = collideModel;
	}

	public String getTexture() {
		return this.texture;
	}

	public void setTexture(final String texture) {
		this.texture = texture;
	}

	public String getDetailMapTexture() {
		return this.detailMapTexture;
	}

	public void setDetailMapTexture(final String detailMapTexture) {
		this.detailMapTexture = detailMapTexture;
	}

	public float getMass() {
		return this.mass;
	}

	public void setMass(final float mass) {
		this.mass = mass;
	}

	@Override
	public AbstractEntity create(final Wanderer game) {
		return new Prop(game, this);
	}
}
