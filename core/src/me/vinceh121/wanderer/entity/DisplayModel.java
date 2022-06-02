package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.WandererConstants;

/**
 * Name to avoid confusion with {@link Model}
 */
public class DisplayModel {
	/**
	 * This model's transform relative to the entity's
	 */
	private final Matrix4 relativeTransform = new Matrix4();
	private final Matrix4 absoluteTransform = new Matrix4();
	private final Array<Attribute> textureAttributes = new Array<>();
	private String displayModel, displayTexture;
	private ModelInstance cacheDisplayModel;

	public DisplayModel() {
	}

	public DisplayModel(String displayModel, String displayTexture) {
		this.displayModel = displayModel;
		this.displayTexture = displayTexture;
	}
	
	public DisplayModel(String displayModel, String displayTexture, Matrix4 relativeTransform) {
		this(displayModel, displayTexture);
		this.relativeTransform.set(relativeTransform);
	}
	
	public DisplayModel(DisplayModel from) {
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
			texture.setFilter(TextureFilter.MipMapNearestLinear, TextureFilter.Linear);

			instance.materials.get(0).set(TextureAttribute.createDiffuse(texture));
			instance.materials.get(0).set(this.textureAttributes); // this is called set but it's more like add
		}
		this.setCacheDisplayModel(instance);
		this.getCacheDisplayModel().transform = this.absoluteTransform;
	}

	public void updateTransform(Matrix4 entityTrans) {
		this.absoluteTransform.set(entityTrans);

		this.absoluteTransform.translate(relativeTransform.getTranslation(new Vector3()));

		this.absoluteTransform.rotate(relativeTransform.getRotation(new Quaternion()));

		this.absoluteTransform.scl(relativeTransform.getScale(new Vector3()));

		if (this.cacheDisplayModel != null) {
			this.cacheDisplayModel.transform = this.absoluteTransform;
		}
	}

	public String getDisplayModel() {
		return displayModel;
	}

	public void setDisplayModel(String displayModel) {
		this.displayModel = displayModel;
	}

	public String getDisplayTexture() {
		return displayTexture;
	}

	public void setDisplayTexture(String displayTexture) {
		this.displayTexture = displayTexture;
	}

	public ModelInstance getCacheDisplayModel() {
		return cacheDisplayModel;
	}

	public void setCacheDisplayModel(ModelInstance cacheDisplayModel) {
		this.cacheDisplayModel = cacheDisplayModel;
	}

	public Matrix4 getAbsoluteTransform() {
		return absoluteTransform;
	}

	public void setAbsoluteTransform(Matrix4 trans) {
		this.absoluteTransform.set(trans);
	}

	public Matrix4 getRelativeTransform() {
		return relativeTransform;
	}

	public void setRelativeTransform(Matrix4 trans) {
		this.relativeTransform.set(trans);
	}

	public Array<Attribute> getTextureAttributes() {
		return textureAttributes;
	}

	public void addTextureAttribute(Attribute value) {
		textureAttributes.add(value);
	}

	public boolean removeTextureAttribute(Attribute value) {
		return textureAttributes.removeValue(value, false);
	}

	public Attribute removeTextureAttribute(int index) {
		return textureAttributes.removeIndex(index);
	}
}
