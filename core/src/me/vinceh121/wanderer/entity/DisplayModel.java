package me.vinceh121.wanderer.entity;

import java.util.ArrayList;
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
	@JsonIgnore
	private final Matrix4 absoluteTransform = new Matrix4();
	private final List<Attribute> textureAttributes = new ArrayList<>();
	private String displayModel, displayTexture;
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
	}

	public void render(final ModelBatch batch, final Environment env) {
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
	}

	public void updateTransform(final Matrix4 entityTrans) {
		this.absoluteTransform.set(entityTrans);

		this.absoluteTransform.mul(this.relativeTransform);

		if (this.cacheDisplayModel != null) {
			this.cacheDisplayModel.transform = this.absoluteTransform;
		}
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
	}

	public boolean removeTextureAttribute(final Attribute value) {
		return this.textureAttributes.remove(value);
	}

	public Attribute removeTextureAttribute(final int index) {
		return this.textureAttributes.remove(index);
	}
}
