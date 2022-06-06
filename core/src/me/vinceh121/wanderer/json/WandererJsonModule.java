package me.vinceh121.wanderer.json;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class WandererJsonModule {
	public static void registerModules(ObjectMapper mapper) {
		final SimpleModule mod = new SimpleModule("WandererJson");
		mod.addSerializer(Array.class, new GdxArraySerializer());
		mod.addDeserializer(Array.class, new GdxArrayDeserializer());

		mod.addDeserializer(Vector3.class, new Vector3Deserializer());
		mod.addSerializer(Vector3.class, new Vector3Serializer());

		mod.addDeserializer(Quaternion.class, new QuaternionDeserializer());
		mod.addSerializer(Quaternion.class, new QuaternionSerializer());

		mod.addSerializer(Matrix4.class, new Matrix4Serializer());
		mod.addDeserializer(Matrix4.class, new Matrix4Deserializer());
		mapper.registerModule(mod);
	}
}
