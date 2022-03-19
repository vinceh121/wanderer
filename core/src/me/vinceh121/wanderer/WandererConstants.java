package me.vinceh121.wanderer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public final class WandererConstants {
	public static final AssetManager ASSET_MANAGER = new AssetManager();

	public static Skin getDevSkin() {
		return WandererConstants.ASSET_MANAGER.get("skins/default/uiskin.json", Skin.class);
	}

	static {
		WandererConstants.ASSET_MANAGER.load("skins/default/uiskin.json", Skin.class);
		WandererConstants.ASSET_MANAGER.finishLoading();
	}
}
