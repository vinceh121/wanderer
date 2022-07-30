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
	public void serialize(final Array value, final JsonGenerator gen, final SerializerProvider provider)
			throws IOException {
		gen.writeStartArray();
		for (final Object e : value) {
			if (e == null) {
				provider.defaultSerializeNull(gen);
			} else {
				final Class<?> cc = e.getClass();
				final JsonSerializer<Object> serializer = provider.findValueSerializer(cc);
				if (serializer == null) {
					throw new IllegalStateException("Jackson doesn't know how to serialize " + cc);
				}
				serializer.serialize(e, gen, provider);
			}
		}
		gen.writeEndArray();
	}
}
