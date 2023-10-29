package me.vinceh121.wanderer.entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.annotation.JsonIgnore;

import me.vinceh121.wanderer.WandererConstants;

/**
 * Name to avoid confusion with {@link Model}
 */
public class DisplayModel {
	/**
	 * This model's transform relative to the entity's
	 */
	private final Matrix4 relativeTransform = new Matrix4();
	private final Array<DisplayModel> children = new Array<>();
	@JsonIgnore
	private final Matrix4 absoluteTransform = new Matrix4();
	private final List<Attribute> textureAttributes = new ArrayList<>();
	private String displayModel, displayTexture, animationChannel;
	@JsonIgnore
	private ModelInstance cacheDisplayModel;

	public DisplayModel() {
	}

	public DisplayModel(final String displayModel, final String displayTexture) {
		this(displayModel, displayTexture, new Matrix4());
	}

	public DisplayModel(final String displayModel, final String displayTexture, final Matrix4 relativeTransform) {
		this.displayModel = displayModel;
		this.displayTexture = displayTexture;
		this.relativeTransform.set(relativeTransform);
	}

	public DisplayModel(final DisplayModel from) {
		this.setRelativeTransform(from.getRelativeTransform());
		this.setAbsoluteTransform(from.getAbsoluteTransform());
		this.textureAttributes.addAll(from.textureAttributes);
		this.setDisplayModel(from.getDisplayModel());
		this.setDisplayTexture(from.getDisplayTexture());
		this.setCacheDisplayModel(from.getCacheDisplayModel());
		this.setAnimationChannel(from.getAnimationChannel());

		for (DisplayModel fromChild : from.getChildren()) {
			this.children.add(new DisplayModel(fromChild));
		}
	}

	public void render(final ModelBatch batch, final Environment env) {
		for (DisplayModel child : this.children) {
			child.render(batch, env);
		}

		if (this.displayModel == null) {
			return;
		}

		if (this.getCacheDisplayModel() != null) {
			batch.render(this.getCacheDisplayModel(), env);
		} else {
			if (WandererConstants.ASSET_MANAGER.isLoaded(this.getDisplayModel())
					&& (this.displayTexture == null || WandererConstants.ASSET_MANAGER.isLoaded(this.displayTexture))) {
				this.loadDisplayModel();
				batch.render(this.getCacheDisplayModel(), env);
			} else {
				WandererConstants.ASSET_MANAGER.load(this.getDisplayModel(), Model.class);
				if (this.displayTexture != null) {
					WandererConstants.ASSET_MANAGER.load(this.displayTexture, Texture.class, WandererConstants.MIPMAPS);
				}
			}
		}
	}

	public void loadDisplayModel() {
		final Model model = WandererConstants.ASSET_MANAGER.get(this.getDisplayModel(), Model.class);
		final ModelInstance instance = new ModelInstance(model);

		if (this.displayTexture != null) {
			final Texture texture = WandererConstants.ASSET_MANAGER.get(this.displayTexture, Texture.class);
			texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);

			instance.materials.get(0).set(TextureAttribute.createDiffuse(texture));
			instance.materials.get(0).set(this.textureAttributes); // this is called set but it's more like add
		}

		this.setCacheDisplayModel(instance);
		this.getCacheDisplayModel().transform = this.absoluteTransform;

		for (DisplayModel child : this.children) {
			child.loadDisplayModel();
		}
	}

	public void updateTransform(final Matrix4 entityTrans) {
		this.absoluteTransform.set(entityTrans);

		this.absoluteTransform.mul(this.relativeTransform);

		if (this.cacheDisplayModel != null) {
			this.cacheDisplayModel.transform = this.absoluteTransform;
		}

		for (DisplayModel child : this.children) {
			child.updateTransform(this.absoluteTransform);
		}
	}

	public Array<DisplayModel> getChildren() {
		return this.children;
	}

	public void setChildren(Array<DisplayModel> children) {
		this.children.clear();
		this.children.addAll(children);
	}

	public String getDisplayModel() {
		return this.displayModel;
	}

	public void setDisplayModel(final String displayModel) {
		this.displayModel = displayModel;
	}

	public String getDisplayTexture() {
		return this.displayTexture;
	}

	public void setDisplayTexture(final String displayTexture) {
		this.displayTexture = displayTexture;
	}

	public String getAnimationChannel() {
		return this.animationChannel;
	}

	public void setAnimationChannel(final String animationChannel) {
		this.animationChannel = animationChannel;
	}

	@JsonIgnore
	public ModelInstance getCacheDisplayModel() {
		return this.cacheDisplayModel;
	}

	@JsonIgnore
	public void setCacheDisplayModel(final ModelInstance cacheDisplayModel) {
		this.cacheDisplayModel = cacheDisplayModel;
	}

	@JsonIgnore
	public Matrix4 getAbsoluteTransform() {
		return this.absoluteTransform;
	}

	@JsonIgnore
	public void setAbsoluteTransform(final Matrix4 trans) {
		this.absoluteTransform.set(trans);
	}

	public Matrix4 getRelativeTransform() {
		return this.relativeTransform;
	}

	public void setRelativeTransform(final Matrix4 trans) {
		this.relativeTransform.set(trans);
	}

	public List<Attribute> getTextureAttributes() {
		return this.textureAttributes;
	}

	public void addTextureAttribute(final Attribute value) {
		this.textureAttributes.add(value);
		
		if (this.cacheDisplayModel != null) {
			this.cacheDisplayModel.materials.get(0).set(value);
		}
	}

	public void removeTextureAttribute(final Attribute value) {
		this.textureAttributes.remove(value);
		
		if (this.cacheDisplayModel != null) {
			this.cacheDisplayModel.materials.get(0).remove(value.type);
		}
	}

	@Override
	public String toString() {
		return "DisplayModel [relativeTransform=" + this.relativeTransform + ", absoluteTransform="
				+ this.absoluteTransform + ", displayModel=" + this.displayModel + ", displayTexture="
				+ this.displayTexture + ", animationChannel=" + this.animationChannel + "]";
	}
	
	public static LinkedList<DisplayModel> flattenModels(Iterable<DisplayModel> models) {
		LinkedList<DisplayModel> flatModels = new LinkedList<>();

		for (final DisplayModel root : models) {
			flattenModels(flatModels, root);
		}

		return flatModels;
	}
	
	private static void flattenModels(LinkedList<DisplayModel> flatModels, DisplayModel mdl) {
		flatModels.add(mdl);

		for (final DisplayModel child : mdl.getChildren()) {
			flattenModels(flatModels, child);
		}
	}
}
