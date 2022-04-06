package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.math.Vector2;

public class TiledMaterialAttribute extends Attribute {
	public final static String TiledMaterialAlias = "TiledMaterial";
	public final static long TiledMaterial = register(TiledMaterialAlias);

	public static TiledMaterialAttribute create(Texture texture) {
		return new TiledMaterialAttribute(TiledMaterial, new TextureDescriptor<>(texture));
	}

	public static TiledMaterialAttribute create(Texture texture, float opacity) {
		return new TiledMaterialAttribute(TiledMaterial, new TextureDescriptor<>(texture), opacity);
	}
	
	public static TiledMaterialAttribute create(Texture texture, float opacity, Vector2 ratio) {
		return new TiledMaterialAttribute(TiledMaterial, new TextureDescriptor<>(texture), opacity, ratio);
	}

	private final TextureDescriptor<Texture> textureDescriptor;
	private final float opacity;
	private final Vector2 ratio;

	protected TiledMaterialAttribute(long type) {
		this(type, new TextureDescriptor<>());
	}

	protected TiledMaterialAttribute(long type, TextureDescriptor<Texture> textureDescriptor) {
		this(type, textureDescriptor, 0.5f);
	}

	protected TiledMaterialAttribute(long type, TextureDescriptor<Texture> textureDescriptor, float opacity) {
		this(type, textureDescriptor, opacity, new Vector2(1, 1));
	}

	protected TiledMaterialAttribute(long type, TextureDescriptor<Texture> textureDescriptor, float opacity,
			Vector2 ratio) {
		super(type);
		this.textureDescriptor = textureDescriptor;
		this.opacity = opacity;
		this.ratio = ratio;
	}

	@Override
	public int compareTo(Attribute o) {
		if (type != o.type)
			return type < o.type ? -1 : 1;
		TextureAttribute other = (TextureAttribute) o;
		return textureDescriptor.compareTo(other.textureDescription);
	}

	@Override
	public Attribute copy() {
		return new TiledMaterialAttribute(this.type, this.textureDescriptor);
	}

	public TextureDescriptor<Texture> getTextureDescriptor() {
		return textureDescriptor;
	}

	public float getOpacity() {
		return opacity;
	}

	public Vector2 getRatio() {
		return ratio;
	}
}