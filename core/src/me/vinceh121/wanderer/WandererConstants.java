package me.vinceh121.wanderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.vinceh121.wanderer.json.WandererJsonModule;
import me.vinceh121.wanderer.platform.audio.AudioSystem3D;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.platform.audio.Sound3DLoader;

public final class WandererConstants {
	public static final AssetManager ASSET_MANAGER = new AssetManager();
	public static final TextureParameter MIPMAPS = new TextureParameter();
	public static final ObjectMapper MAPPER = new ObjectMapper();
	public static final AudioSystem3D AUDIO = (AudioSystem3D) Gdx.audio;

	public static Skin getDevSkin() {
		return WandererConstants.ASSET_MANAGER.get("skins/default/uiskin.json", Skin.class);
	}

	static {
		WandererJsonModule.registerModules(WandererConstants.MAPPER);

		WandererConstants.ASSET_MANAGER.setLoader(Sound3D.class, new Sound3DLoader(WandererConstants.ASSET_MANAGER.getFileHandleResolver()));

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
			WandererConstants.ASSET_MANAGER.load(skin, Skin.class);
		}
		WandererConstants.ASSET_MANAGER.finishLoading();
	}
}
