package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.glx.TiledMaterialAttribute;

public class AttributeSerialiser extends StdSerializer<Attribute> {
	private static final long serialVersionUID = 1L;

	public AttributeSerialiser() {
		super(Attribute.class);
	}

	@Override
	public void serialize(final Attribute att, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
		final String type = Attribute.getAttributeAlias(att.type);
		gen.writeStartObject(att);
		gen.writeStringField("@class", att.getClass().getSimpleName());
		gen.writeStringField("type", type);

		if (att instanceof IntAttribute && att.type == IntAttribute.CullFace) {
			gen.writeNumberField("value", ((IntAttribute) att).value);
		} else if (att instanceof DepthTestAttribute && att.type == DepthTestAttribute.Type) {
			final DepthTestAttribute depth = (DepthTestAttribute) att;
			gen.writeNumberField("depthFunc", depth.depthFunc);
			gen.writeNumberField("depthRangeNear", depth.depthRangeNear);
			gen.writeNumberField("depthRangeFar", depth.depthRangeFar);
			gen.writeBooleanField("depthMask", depth.depthMask);
		} else if (att instanceof FloatAttribute && att.type == FloatAttribute.AlphaTest) {
			gen.writeNumberField("value", ((FloatAttribute) att).value);
		} else if (att instanceof TiledMaterialAttribute && att.type == TiledMaterialAttribute.TiledMaterial) {
			final TiledMaterialAttribute tiled = (TiledMaterialAttribute) att;
			gen.writeStringField("texture",
					WandererConstants.ASSET_MANAGER.getAssetFileName(tiled.getTextureDescriptor().texture));
			gen.writeNumberField("opacity", tiled.getOpacity());
			gen.writeArrayFieldStart("ratio");
			gen.writeNumber(tiled.getRatio().x);
			gen.writeNumber(tiled.getRatio().y);
			gen.writeEndArray();
		} else if (att instanceof BlendingAttribute && att.type == BlendingAttribute.Type) {
			final BlendingAttribute blend = (BlendingAttribute) att;
			gen.writeBooleanField("blended", blend.blended);
			gen.writeNumberField("sourceFunction", blend.sourceFunction);
			gen.writeNumberField("destFunction", blend.destFunction);
			gen.writeNumberField("opacity", blend.opacity);
		} else {
			throw new IllegalStateException(
					"Cannot serialize Attribute of class " + att.getClass() + " and type " + type);
		}
		gen.writeEndObject();
	}
}
