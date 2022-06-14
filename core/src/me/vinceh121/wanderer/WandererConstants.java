package me.vinceh121.wanderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.vinceh121.wanderer.json.WandererJsonModule;

public final class WandererConstants {
	public static final AssetManager ASSET_MANAGER = new AssetManager();
	public static final TextureParameter MIPMAPS = new TextureParameter();
	public static final ObjectMapper MAPPER = new ObjectMapper();

	public static Skin getDevSkin() {
		return WandererConstants.ASSET_MANAGER.get("skins/default/uiskin.json", Skin.class);
	}

	static {
		WandererJsonModule.registerModules(MAPPER);

		WandererConstants.MIPMAPS.genMipMaps = true;

		final JsonReader jsonReader = new JsonReader();

		final JsonValue preload = jsonReader.parse(Gdx.files.internal("preload.json"));
		for (final String sound : preload.get("sounds").asStringArray()) {
			WandererConstants.ASSET_MANAGER.load(sound, Sound.class);
		}
		for (final String texture : preload.get("textures").asStringArray()) {
			WandererConstants.ASSET_MANAGER.load(texture, Texture.class);
		}
		for (final String skin : preload.get("skins").asStringArray()) {
			WandererConstants.ASSET_MANAGER.load(skin, Skin.class);
		}
		WandererConstants.ASSET_MANAGER.finishLoading();
	}
}
