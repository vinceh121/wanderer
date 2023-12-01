package me.vinceh121.wanderer.building;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.IPrototype;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.DisplayModel;

public class IslandPrototype implements IPrototype {
	private Array<DisplayModel> displayModels = new Array<>();
	private String collisionModel;
	private Array<Slot> slots = new Array<>();
	private Vector3 placeCameraPosition = new Vector3(), placeCameraDirection = new Vector3();

	public IslandPrototype() {
	}

	public IslandPrototype(final Array<Slot> slots) {
		this.slots = slots;
	}

	public IslandPrototype(final Slot... slots) {
		this.slots = new Array<>(slots);
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

	public Array<DisplayModel> getDisplayModels() {
		return this.displayModels;
	}

	public void setDisplayModels(final Array<DisplayModel> displayModels) {
		this.displayModels = displayModels;
	}

	public String getCollisionModel() {
		return this.collisionModel;
	}

	public void setCollisionModel(final String collisionModel) {
		this.collisionModel = collisionModel;
	}

	public Array<Slot> getSlots() {
		return this.slots;
	}

	public void setSlots(final Array<Slot> slots) {
		this.slots = slots;
	}

	public Vector3 getPlaceCameraPosition() {
		return this.placeCameraPosition;
	}

	public void setPlaceCameraPosition(final Vector3 placeCameraPosition) {
		this.placeCameraPosition = placeCameraPosition;
	}

	public Vector3 getPlaceCameraDirection() {
		return this.placeCameraDirection;
	}

	public void setPlaceCameraDirection(final Vector3 placeCameraDirection) {
		this.placeCameraDirection = placeCameraDirection;
	}

	@Override
	public AbstractEntity create(final Wanderer game) {
		return new Island(game, this);
	}

	@Override
	public void getAssetsToLoad(final List<AssetDescriptor<?>> descriptors) {
		descriptors.add(new AssetDescriptor<>(this.collisionModel, Model.class));

		for (final DisplayModel mdl : DisplayModel.flattenModels(this.displayModels)) {
			descriptors.add(new AssetDescriptor<>(mdl.getDisplayModel(), Model.class));
			descriptors.add(new AssetDescriptor<>(mdl.getDisplayTexture(), Texture.class));
		}
	}
}
