package me.vinceh121.wanderer.artifact;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.platform.audio.Sound3D;

public class BackpackArtifact extends AbstractArtifactEntity {
	private static final ArtifactMeta BACKPACK_META = new ArtifactMeta();

	public BackpackArtifact(final Wanderer game) {
		super(game, BackpackArtifact.BACKPACK_META);
	}

	@Override
	public boolean onPickUp(final Wanderer game, final CharacterW chara) {
		WandererConstants.ASSET_MANAGER.get("orig/feedback/backpack_active.wav", Sound3D.class).play();
		game.showMessage("Backpack activated!");
		// TODO enable backpack for player
		return true;
	}

	static {
		BackpackArtifact.BACKPACK_META.setArtifactModel("orig/boosterartefact.n/kugel.obj");
		BackpackArtifact.BACKPACK_META.setArtifactTexture("orig/boosterartefact.n/texturenone.ktx");
	}
}
