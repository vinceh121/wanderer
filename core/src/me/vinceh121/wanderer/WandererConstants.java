package me.vinceh121.wanderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import me.vinceh121.wanderer.character.CharacterMeta;

public final class WandererConstants {
	public static final AssetManager ASSET_MANAGER = new AssetManager();
	public static final Array<CharacterMeta> CHARACTER_METAS = new Array<>();
	public static final TextureParameter MIPMAPS = new TextureParameter();

	public static Skin getDevSkin() {
		return WandererConstants.ASSET_MANAGER.get("skins/default/uiskin.json", Skin.class);
	}

	static {
		WandererConstants.MIPMAPS.genMipMaps = true;

		final Json json = new Json(OutputType.json);
		final JsonReader jsonReader = new JsonReader();

		final String[] metas = { "goliath", "john", "susie" };
		for (final String m : metas) {
			WandererConstants.CHARACTER_METAS
					.add(json.fromJson(CharacterMeta.class, Gdx.files.internal("characters/" + m + ".json")));
		}

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
