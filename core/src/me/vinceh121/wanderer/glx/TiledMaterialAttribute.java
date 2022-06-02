package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.math.Vector2;

public class TiledMaterialAttribute extends Attribute {
	public final static String TiledMaterialAlias = "TiledMaterial";
	public final static long TiledMaterial = Attribute.register(TiledMaterialAttribute.TiledMaterialAlias);

	public static TiledMaterialAttribute create(final Texture texture) {
		return new TiledMaterialAttribute(TiledMaterialAttribute.TiledMaterial, new TextureDescriptor<>(texture));
	}

	public static TiledMaterialAttribute create(final Texture texture, final float opacity) {
		return new TiledMaterialAttribute(TiledMaterialAttribute.TiledMaterial, new TextureDescriptor<>(texture),
				opacity);
	}

	public static TiledMaterialAttribute create(final Texture texture, final float opacity, final Vector2 ratio) {
		return new TiledMaterialAttribute(TiledMaterialAttribute.TiledMaterial, new TextureDescriptor<>(texture),
				opacity, ratio);
	}

	private final TextureDescriptor<Texture> textureDescriptor;
	private final float opacity;
	private final Vector2 ratio;

	protected TiledMaterialAttribute(final long type) {
		this(type, new TextureDescriptor<>());
	}

	protected TiledMaterialAttribute(final long type, final TextureDescriptor<Texture> textureDescriptor) {
		this(type, textureDescriptor, 0.5f);
	}

	protected TiledMaterialAttribute(final long type, final TextureDescriptor<Texture> textureDescriptor,
			final float opacity) {
		this(type, textureDescriptor, opacity, new Vector2(1, 1));
	}

	protected TiledMaterialAttribute(final long type, final TextureDescriptor<Texture> textureDescriptor,
			final float opacity, final Vector2 ratio) {
		super(type);
		this.textureDescriptor = textureDescriptor;
		this.opacity = opacity;
		this.ratio = ratio;
	}

	@Override
	public int compareTo(final Attribute o) {
		if (this.type != o.type) {
			return this.type < o.type ? -1 : 1;
		}
		final TextureAttribute other = (TextureAttribute) o;
		return this.textureDescriptor.compareTo(other.textureDescription);
	}

	@Override
	public Attribute copy() {
		return new TiledMaterialAttribute(this.type, this.textureDescriptor);
	}

	public TextureDescriptor<Texture> getTextureDescriptor() {
		return this.textureDescriptor;
	}

	public float getOpacity() {
		return this.opacity;
	}

	public Vector2 getRatio() {
		return this.ratio;
	}
}
