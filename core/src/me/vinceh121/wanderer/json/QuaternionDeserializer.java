package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.math.Quaternion;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class QuaternionDeserializer extends StdDeserializer<Quaternion> {
	private static final long serialVersionUID = 1L;

	public QuaternionDeserializer() {
		super(Quaternion.class);
	}

	@Override
	public Quaternion deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		if (p.currentToken() != JsonToken.START_ARRAY) {
			throw new IllegalStateException("Expected quaternion array start, got garbage");
		}
		float[] vals = new float[4];
		for (int i = 0; i < 4; i++) {
			if (p.nextToken() != JsonToken.VALUE_NUMBER_FLOAT) {
				throw new IllegalStateException("Expected quaternion float value, got garbage");
			}
			vals[i] = p.getFloatValue();
		}
		if (p.nextToken() != JsonToken.END_ARRAY) {
			throw new JsonParseException(p, "Expected token END_ARRAY, got " + p.currentToken());
		}
		return new Quaternion(vals[0], vals[1], vals[2], vals[3]);
	}
}
