package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.math.Quaternion;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class QuaternionSerializer extends StdSerializer<Quaternion> {
	private static final long serialVersionUID = 1L;

	public QuaternionSerializer() {
		super(Quaternion.class);
	}

	@Override
	public void serialize(final Quaternion value, final JsonGenerator gen, final SerializerProvider provider)
			throws IOException {
		gen.writeStartArray();
		gen.writeNumber(value.x);
		gen.writeNumber(value.y);
		gen.writeNumber(value.z);
		gen.writeNumber(value.w); // W is last!
		gen.writeEndArray();
	}
}
