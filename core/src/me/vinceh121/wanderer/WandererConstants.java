package me.vinceh121.wanderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import me.vinceh121.wanderer.glx.GLBModelLoader;
import me.vinceh121.wanderer.glx.GLTFModelLoader;
import me.vinceh121.wanderer.json.WandererJsonModule;
import me.vinceh121.wanderer.platform.audio.AudioSystem3D;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.platform.audio.Sound3DLoader;
import me.vinceh121.wanderer.ui.WSkin;
import me.vinceh121.wanderer.ui.WSkinLoader;
import me.vinceh121.wanderer.util.AssetFileHandleResolver;

public final class WandererConstants {
	private static final Logger LOG = LogManager.getLogger(WandererConstants.class);
	public static final AssetManager ASSET_MANAGER = new AssetManager(new AssetFileHandleResolver());
	public static final TextureParameter MIPMAPS = new TextureParameter();
	public static final ObjectMapper MAPPER = new ObjectMapper(), SAVE_MAPPER;
	public static final AudioSystem3D AUDIO = (AudioSystem3D) Gdx.audio;
	public static final Texture BLACK_PIXEL;

	public static WSkin getDevSkin() {
		return WandererConstants.ASSET_MANAGER.get("skins/default/uiskin.json", WSkin.class);
	}

	public static WSkin getUISkin() {
		return WandererConstants.ASSET_MANAGER.get("skins/pn/uiskin.json", WSkin.class);
	}

	public static Model getAudioDebug() {
		return WandererConstants.getAssetOrHotload("audio-debug.glb", Model.class);
	}

	public static Model getCircleDebug() {
		return WandererConstants.getAssetOrHotload("circle-debug.glb", Model.class);
	}

	public static <T> T getAssetOrHotload(final String path, final Class<T> type) {
		if (!WandererConstants.ASSET_MANAGER.isLoaded(path, type)) {
			WandererConstants.ASSET_MANAGER.load(path, type);
			WandererConstants.LOG.warn("Hot loading asset {} of type {}", path, type);
			WandererConstants.ASSET_MANAGER.finishLoadingAsset(path);
		}

		return WandererConstants.ASSET_MANAGER.get(path, type);
	}

	static {
		WandererJsonModule.registerModules(WandererConstants.MAPPER);
		WandererConstants.MAPPER.enable(JsonParser.Feature.ALLOW_COMMENTS);

		SAVE_MAPPER = JsonMapper.builder().enable(MapperFeature.USE_STATIC_TYPING).build();
//		SAVE_MAPPER.activateDefaultTyping(BasicPolymorphicTypeValidator.builder().allowIfSubType(new TypeMatcher() {
//			@Override
//			public boolean match(MapperConfig<?> config, Class<?> clazz) {
//				return clazz.getCanonicalName().startsWith("me.vinceh121.wanderer");
//			}
//		})
//			.allowIfSubType(Array.class)
//			.allowIfSubType(Matrix4.class)
//			.allowIfSubType(Quaternion.class)
//			.allowIfSubType(Vector3.class)
//			.build(), DefaultTyping.NON_FINAL, As.PROPERTY);
		WandererJsonModule.registerModules(WandererConstants.SAVE_MAPPER);
		WandererConstants.SAVE_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		// need to do this so Log4J can choose the
		// actual log level
		WandererConstants.ASSET_MANAGER.getLogger().setLevel(com.badlogic.gdx.utils.Logger.DEBUG);
		WandererConstants.ASSET_MANAGER.setLoader(Sound3D.class,
				new Sound3DLoader(WandererConstants.ASSET_MANAGER.getFileHandleResolver()));
		WandererConstants.ASSET_MANAGER.setLoader(Model.class, ".gltf", new GLTFModelLoader());
		WandererConstants.ASSET_MANAGER.setLoader(Model.class, ".glb", new GLBModelLoader());

		WandererConstants.ASSET_MANAGER.setLoader(WSkin.class,
				new WSkinLoader(WandererConstants.ASSET_MANAGER.getFileHandleResolver()));

		WandererConstants.MIPMAPS.genMipMaps = true;

		final JsonReader jsonReader = new JsonReader();

		final JsonValue preload = jsonReader.parse(Gdx.files.internal("preload.json"));
		for (final String sound : preload.get("sounds").asStringArray()) {
			WandererConstants.ASSET_MANAGER.load(sound, Sound3D.class);
		}
		for (final String texture : preload.get("textures").asStringArray()) {
			WandererConstants.ASSET_MANAGER.load(texture, Texture.class);
		}
		for (final String skin : preload.get("skins").asStringArray()) {
			WandererConstants.ASSET_MANAGER.load(skin, WSkin.class);
		}
		for (final String model : preload.get("models").asStringArray()) {
			WandererConstants.ASSET_MANAGER.load(model, Model.class);
		}
		WandererConstants.ASSET_MANAGER.finishLoading();

		final Pixmap blkPix = new Pixmap(1, 1, Format.RGB565);
		BLACK_PIXEL = new Texture(blkPix);
	}
}
