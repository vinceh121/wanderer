package me.vinceh121.wanderer.artifact;

import com.badlogic.gdx.audio.Sound;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.character.CharacterW;

public class BackpackArtifact extends AbstractArtifactEntity {
	private static final ArtifactMeta BACKPACK_META = new ArtifactMeta();

	public BackpackArtifact(Wanderer game) {
		super(game, BACKPACK_META);
	}

	@Override
	public boolean onPickUp(Wanderer game, CharacterW chara) {
		WandererConstants.ASSET_MANAGER.get("orig/feedback/backpack_active.wav", Sound.class).play();
		game.showMessage("Backpack activated!");
		// TODO enable backpack for player
		return true;
	}
	
	static {
		BACKPACK_META.setArtifactModel("orig/boosterartefact.n/kugel.obj");
		BACKPACK_META.setArtifactTexture("orig/boosterartefact.n/texturenone.ktx");
	}
}
