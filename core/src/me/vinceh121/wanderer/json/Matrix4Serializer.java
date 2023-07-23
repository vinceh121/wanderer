package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.math.Matrix4;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class Matrix4Serializer extends StdSerializer<Matrix4> {
	private static final long serialVersionUID = 1L;

	public Matrix4Serializer() {
		super(Matrix4.class);
	}

	@Override
	public void serialize(final Matrix4 value, final JsonGenerator gen, final SerializerProvider provider)
			throws IOException {
		gen.writeStartArray();
		this.writeContent(value, gen, provider);
		gen.writeEndArray();
	}

	@Override
	public void serializeWithType(final Matrix4 value, final JsonGenerator gen, final SerializerProvider serializers,
			final TypeSerializer typeSer) throws IOException {
		final WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_ARRAY));
		gen.setCurrentValue(value);
		this.writeContent(value, gen, serializers);
		typeSer.writeTypeSuffix(gen, typeIdDef);
	}

	private void writeContent(final Matrix4 value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
		for (final float f : value.val) {
			gen.writeNumber(f);
		}
	}
}
