package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class Vector3Serializer extends StdSerializer<Vector3> {
	private static final long serialVersionUID = 1L;

	public Vector3Serializer() {
		super(Vector3.class);
	}

	@Override
	public void serialize(Vector3 value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartArray();
		gen.writeNumber(value.x);
		gen.writeNumber(value.y);
		gen.writeNumber(value.z);
		gen.writeEndArray();
	}
}
