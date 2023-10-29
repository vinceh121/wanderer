package me.vinceh121.wanderer.entity.plane;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.IPrototype;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.platform.audio.Sound3D;

public abstract class AbstractPlanePrototype implements IPrototype {
	private Array<DisplayModel> displayModels = new Array<>();
	private Array<DisplayModel> explosionParts = new Array<>();
	private String collisionModel, engineSound, turboSound, explosionSound;
	private final PlaneSpeedProfile normal = new PlaneSpeedProfile(), turbo = new PlaneSpeedProfile();
	private float maxTurboTime;

	@Override
	public void getAssetsToLoad(List<AssetDescriptor<?>> descriptors) {
		descriptors.add(new AssetDescriptor<>(this.collisionModel, Model.class));

		descriptors.add(new AssetDescriptor<>(this.engineSound, Sound3D.class));
		descriptors.add(new AssetDescriptor<>(this.turboSound, Sound3D.class));
		descriptors.add(new AssetDescriptor<>(this.explosionSound, Sound3D.class));

		for (final DisplayModel mdl : DisplayModel.flattenModels(this.displayModels)) {
			descriptors.add(new AssetDescriptor<>(mdl.getDisplayModel(), Model.class));
			descriptors.add(new AssetDescriptor<>(mdl.getDisplayTexture(), Texture.class));
		}

		for (final DisplayModel mdl : DisplayModel.flattenModels(this.explosionParts)) {
			descriptors.add(new AssetDescriptor<>(mdl.getDisplayModel(), Model.class));
			descriptors.add(new AssetDescriptor<>(mdl.getDisplayTexture(), Texture.class));
		}
	}

	public Array<DisplayModel> getDisplayModels() {
		return displayModels;
	}

	public void setDisplayModels(Array<DisplayModel> displayModels) {
		this.displayModels = displayModels;
	}

	public Array<DisplayModel> getExplosionParts() {
		return explosionParts;
	}

	public void setExplosionParts(Array<DisplayModel> explosionParts) {
		this.explosionParts = explosionParts;
	}

	public String getCollisionModel() {
		return collisionModel;
	}

	public void setCollisionModel(String collisionModel) {
		this.collisionModel = collisionModel;
	}

	public String getEngineSound() {
		return engineSound;
	}

	public void setEngineSound(String engineSound) {
		this.engineSound = engineSound;
	}

	public String getTurboSound() {
		return turboSound;
	}

	public void setTurboSound(String turboSound) {
		this.turboSound = turboSound;
	}

	public String getExplosionSound() {
		return explosionSound;
	}

	public void setExplosionSound(String explosionSound) {
		this.explosionSound = explosionSound;
	}

	public PlaneSpeedProfile getNormal() {
		return this.normal;
	}

	public PlaneSpeedProfile getTurbo() {
		return this.turbo;
	}

	public float getMaxTurboTime() {
		return maxTurboTime;
	}

	public void setMaxTurboTime(float maxTurboTime) {
		this.maxTurboTime = maxTurboTime;
	}
}
