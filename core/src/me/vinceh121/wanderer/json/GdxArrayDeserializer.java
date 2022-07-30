package me.vinceh121.wanderer.json;

import java.io.IOException;

import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

// help taken from https://github.com/spring-projects/spring-hateoas/blob/7a17be558a77b158e7a4c6a40a5e5609fffd91f8/src/main/java/org/springframework/hateoas/mediatype/hal/forms/HalFormsDeserializers.java#L44
public class GdxArrayDeserializer extends ContainerDeserializerBase<Array<Object>> implements ContextualDeserializer {
	private static final long serialVersionUID = 1L;
	private final JavaType valueType;

	public GdxArrayDeserializer() {
		this(TypeFactory.defaultInstance().constructCollectionLikeType(Array.class, Object.class));
	}

	public GdxArrayDeserializer(final JavaType selfType) {
		super(selfType);
		this.valueType = selfType;
	}

	@Override
	public JsonDeserializer<Object> getContentDeserializer() {
		return null;
	}

	@Override
	public Array<Object> deserialize(final JsonParser p, final DeserializationContext ctxt)
			throws IOException, JacksonException {
		if (!p.isExpectedStartArrayToken()) {
			throw new JsonParseException(p, "Excepted array start");
		}
		final JsonDeserializer<Object> deser = ctxt.findRootValueDeserializer(this.valueType);
		final Array<Object> array = new Array<>();

		while (p.nextToken() != JsonToken.END_ARRAY) {
			final Object obj = deser.deserialize(p, ctxt);
			array.add(obj);
		}

		return array;
	}

	@Override
	public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property)
			throws JsonMappingException {
		return new GdxArrayDeserializer(ctxt.getContextualType().containedType(0));
	}
}
