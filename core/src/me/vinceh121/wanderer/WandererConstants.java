package me.vinceh121.wanderer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public final class WandererConstants {
	public static final AssetManager ASSET_MANAGER = new AssetManager();

	public static Skin getDevSkin() {
		return ASSET_MANAGER.get("skins/default/uiskin.json", Skin.class);
	}
	
	static {
		ASSET_MANAGER.load("skins/default/uiskin.json", Skin.class);
		ASSET_MANAGER.finishLoading();
	}
}
