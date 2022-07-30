package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class Matrix4Serializer extends StdSerializer<Matrix4> {
	private static final long serialVersionUID = 1L;

	public Matrix4Serializer() {
		super(Matrix4.class);
	}

	@Override
	public void serialize(final Matrix4 value, final JsonGenerator gen, final SerializerProvider provider)
			throws IOException {
		final JsonSerializer<Object> vector3Ser = provider.findValueSerializer(Vector3.class);
		final JsonSerializer<Object> quaternionSer = provider.findValueSerializer(Quaternion.class);

		final Vector3 translation = value.getTranslation(new Vector3());
		final Quaternion rotation = value.getRotation(new Quaternion());
		final Vector3 scale = value.getScale(new Vector3());

		gen.writeStartObject();

		gen.writeFieldName("translation");
		vector3Ser.serialize(translation, gen, provider);

		gen.writeFieldName("rotation");
		quaternionSer.serialize(rotation, gen, provider);

		gen.writeFieldName("scale");
		vector3Ser.serialize(scale, gen, provider);

		gen.writeEndObject();
	}
}
