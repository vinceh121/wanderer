package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class Vector3Deserializer extends StdDeserializer<Vector3> {
	private static final long serialVersionUID = 1L;

	public Vector3Deserializer() {
		super(Vector3.class);
	}

	@Override
	public Vector3 deserialize(final JsonParser p, final DeserializationContext ctxt)
			throws IOException, JacksonException {
		if (p.currentToken() != JsonToken.START_ARRAY) {
			throw new JsonParseException(p, "Expected vector array start, got " + p.currentToken());
		}
		final float[] vals = new float[3];
		for (int i = 0; i < 3; i++) {
			if (p.nextToken() != JsonToken.VALUE_NUMBER_FLOAT && p.currentToken() != JsonToken.VALUE_NUMBER_INT) {
				throw new JsonParseException(p, "Expected vector float value, got " + p.currentToken());
			}
			vals[i] = p.getFloatValue();
		}
		if (p.nextToken() != JsonToken.END_ARRAY) {
			throw new JsonParseException(p, "Expected token END_ARRAY, got " + p.currentToken());
		}
		return new Vector3(vals);
	}
}
