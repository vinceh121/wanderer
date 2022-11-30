package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.glx.TiledMaterialAttribute;

public class AttributeDeserializer extends StdDeserializer<Attribute> {
	private static final long serialVersionUID = -4897698306503294306L;

	public AttributeDeserializer() {
		super(Attribute.class);
	}

	@Override
	public Attribute deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		final ObjectNode n = p.readValueAsTree();

		final String clazz = n.get("@class").asText();
		final String type = n.get("type").asText();

		final Attribute att;
		if (IntAttribute.class.getSimpleName().equals(clazz) && IntAttribute.CullFaceAlias.equals(type)) {
			att = IntAttribute.createCullFace(n.get("value").asInt());
		} else if (DepthTestAttribute.class.getSimpleName().equals(clazz) && DepthTestAttribute.Alias.equals(type)) {
			att = new DepthTestAttribute(n.get("depthFunc").asInt(),
					n.get("depthRangeNear").asInt(),
					n.get("depthRangeFar").asInt(),
					n.get("depthMask").asBoolean());
		} else if (FloatAttribute.class.getSimpleName().equals(clazz) && FloatAttribute.AlphaTestAlias.equals(type)) {
			att = FloatAttribute.createAlphaTest(n.get("value").floatValue());
		} else if (TiledMaterialAttribute.class.getSimpleName().equals(clazz)
				&& TiledMaterialAttribute.TiledMaterialAlias.equals(type)) {
			final String textureName = n.get("texture").asText();
			if (!WandererConstants.ASSET_MANAGER.isLoaded(textureName, Texture.class)) {
				System.err.println("Hot-loading TiledMaterial texture " + textureName);
				WandererConstants.ASSET_MANAGER.load(textureName, Texture.class);
				WandererConstants.ASSET_MANAGER.finishLoadingAsset(textureName);
			}

			final Texture texture = WandererConstants.ASSET_MANAGER.get(textureName, Texture.class);
			texture.setFilter(TextureFilter.MipMapNearestLinear, TextureFilter.Linear);
			texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

			// TODO depend on proper Vector2 deser
			final Vector2 ratio = new Vector2(n.get("ratio").get(0).floatValue(), n.get("ratio").get(1).floatValue());
			att = TiledMaterialAttribute.create(texture, n.get("opacity").floatValue(), ratio);
		} else if (BlendingAttribute.class.getSimpleName().equals(clazz) && BlendingAttribute.Alias.equals(type)) {
			att = new BlendingAttribute(n.get("blended").asBoolean(),
					n.get("sourceFunction").asInt(),
					n.get("destFunction").asInt(),
					n.get("opacity").floatValue());
		} else {
			throw new IllegalStateException("Cannot deser Attribute of class " + clazz + " and type " + type);
		}
		return att;
	}
}
