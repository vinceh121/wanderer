package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class Vector3Serializer extends StdSerializer<Vector3> {
	private static final long serialVersionUID = 1L;

	public Vector3Serializer() {
		super(Vector3.class);
	}

	@Override
	public void serialize(final Vector3 value, final JsonGenerator gen, final SerializerProvider provider)
			throws IOException {
		gen.writeStartArray();
		gen.writeNumber(value.x);
		gen.writeNumber(value.y);
		gen.writeNumber(value.z);
		gen.writeEndArray();
	}

	@Override
	public void serializeWithType(final Vector3 value, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer)
			throws IOException {
		final WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_ARRAY));
		gen.setCurrentValue(value);
		gen.writeNumber(value.x);
		gen.writeNumber(value.y);
		gen.writeNumber(value.z);
		typeSer.writeTypeSuffix(gen, typeIdDef);
	}
}
