package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

// i don't think that's supposed to be there, but i have no idea on how to do this a better way
// jackson's documentation on custom container serializers is lacking
@SuppressWarnings("rawtypes")
public class GdxArraySerializer extends StdSerializer<Array> {
	private static final long serialVersionUID = 1L;

	public GdxArraySerializer() {
		super(Array.class);
	}

	@Override
	public void serialize(Array value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartArray();
		for (Object e : value) {
			if (e == null) {
				provider.defaultSerializeNull(gen);
			} else {
				Class<?> cc = e.getClass();
				JsonSerializer<Object> serializer = provider.findValueSerializer(cc);
				if (serializer == null) {
					throw new IllegalStateException("Jackson doesn't know how to serialize " + cc);
				}
				serializer.serialize(e, gen, provider);
			}
		}
		gen.writeEndArray();
	}
}
