package me.vinceh121.wanderer.artifact;

import com.badlogic.gdx.audio.Sound;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.character.CharacterW;

public class BackpackArtifact extends ArtifactMeta {

	public BackpackArtifact() {
		super(0, false, "orig/boosterartefact.n/kugel.obj", "orig/boosterartefact.n/texturenone.ktx");
		this.setShrink(false);
	}

	@Override
	public boolean onPickUp(Wanderer game, CharacterW chara) {
		WandererConstants.ASSET_MANAGER.get("orig/feedback/backpack_active.wav", Sound.class).play();
		game.showMessage("Backpack activated!");
		// TODO enable backpack for player
		return true;
	}
}
