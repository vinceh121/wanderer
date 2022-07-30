package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.SimpleType;

public class Matrix4Deserializer extends StdDeserializer<Matrix4> {
	private static final long serialVersionUID = 1L;

	public Matrix4Deserializer() {
		super(Matrix4.class);
	}

	@Override
	public Matrix4 deserialize(final JsonParser p, final DeserializationContext ctxt)
			throws IOException, JacksonException {
		final JsonDeserializer<Object> vector3Deser = ctxt
			.findNonContextualValueDeserializer(SimpleType.constructUnsafe(Vector3.class));
		final JsonDeserializer<Object> quaternionDeser = ctxt
			.findNonContextualValueDeserializer(SimpleType.constructUnsafe(Quaternion.class));

		Vector3 translation = new Vector3();
		Quaternion rotation = new Quaternion();
		Vector3 scale = new Vector3();

		if (!p.isExpectedStartObjectToken()) {
			throw new IllegalStateException("Wrong start token for matrix4");
		}

		for (int i = 0; i < 3; i++) {
			final String field = p.nextFieldName();
			if (field == null) {
				throw new JsonParseException(p, "Expected field name, got " + p.getCurrentToken());
			}
			p.nextToken();
			switch (field) {
			case "translation":
				translation = (Vector3) vector3Deser.deserialize(p, ctxt);
				break;
			case "rotation":
				rotation = (Quaternion) quaternionDeser.deserialize(p, ctxt);
				break;
			case "scale":
				scale = (Vector3) vector3Deser.deserialize(p, ctxt);
				break;
			default:
				throw new IllegalStateException(
						"Expected field of either 'translation', 'rotation' or 'scale'. Got invalid field: " + field);
			}
		}

		if (p.nextToken() != JsonToken.END_OBJECT) {
			throw new IllegalStateException("Wrong end token for matrix4");
		}

		return new Matrix4(translation, rotation, scale);
	}
}
