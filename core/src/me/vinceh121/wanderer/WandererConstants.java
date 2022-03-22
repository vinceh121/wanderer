package me.vinceh121.wanderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import me.vinceh121.wanderer.character.CharacterMeta;

public final class WandererConstants {
	public static final AssetManager ASSET_MANAGER = new AssetManager();
	public static final Array<CharacterMeta> CHARACTER_METAS = new Array<>();

	public static Skin getDevSkin() {
		return WandererConstants.ASSET_MANAGER.get("skins/default/uiskin.json", Skin.class);
	}

	static {
		WandererConstants.ASSET_MANAGER.load("skins/default/uiskin.json", Skin.class);
		WandererConstants.ASSET_MANAGER.finishLoading();

		final Json json = new Json(OutputType.json);

		final String[] metas = { "goliath", "john", "susie" };
		for (String m : metas) {
			CHARACTER_METAS.add(json.fromJson(CharacterMeta.class, Gdx.files.internal("characters/" + m + ".json")));
		}
	}
}
