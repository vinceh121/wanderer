package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonSerializer;
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
		gen.writeStartObject();
		this.writeContent(value, gen, provider);
		gen.writeEndObject();
	}

	@Override
	public void serializeWithType(Matrix4 value, JsonGenerator gen, SerializerProvider serializers,
			TypeSerializer typeSer) throws IOException {
		WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_OBJECT));
		gen.setCurrentValue(value);
		this.writeContent(value, gen, serializers);
		typeSer.writeTypeSuffix(gen, typeIdDef);
	}

	private void writeContent(Matrix4 value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		final JsonSerializer<Object> vector3Ser = provider.findValueSerializer(Vector3.class);
		final JsonSerializer<Object> quaternionSer = provider.findValueSerializer(Quaternion.class);

		final Vector3 translation = value.getTranslation(new Vector3());
		final Quaternion rotation = value.getRotation(new Quaternion());
		final Vector3 scale = value.getScale(new Vector3());

		gen.writeFieldName("translation");
		vector3Ser.serialize(translation, gen, provider);

		gen.writeFieldName("rotation");
		quaternionSer.serialize(rotation, gen, provider);

		gen.writeFieldName("scale");
		vector3Ser.serialize(scale, gen, provider);
	}
}
