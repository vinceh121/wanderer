package me.vinceh121.wanderer.artifact;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.platform.audio.Sound3D;

public class BackpackArtifact extends AbstractArtifactEntity {
	private static final ArtifactPrototype BACKPACK_PROTOTYPE = new ArtifactPrototype() { // FIXME move to a JSON
		@Override
		public AbstractEntity create(final Wanderer game) {
			return new BackpackArtifact(game);
		}
	};

	public BackpackArtifact(final Wanderer game) {
		super(game, BackpackArtifact.BACKPACK_PROTOTYPE);
	}

	@Override
	public boolean onPickUp(final Wanderer game, final CharacterW chara) {
		WandererConstants.ASSET_MANAGER.get("orig/feedback/backpack_active.wav", Sound3D.class)
			.playGeneral()
			.setDisposeOnStop(true);
		game.showMessage("Backpack activated!");
		// TODO enable backpack for player
		return true;
	}

	static {
		BackpackArtifact.BACKPACK_PROTOTYPE.setArtifactModel("orig/boosterartefact.n/kugel.obj");
		BackpackArtifact.BACKPACK_PROTOTYPE.setArtifactTexture("orig/boosterartefact.n/texturenone.ktx");
	}
}
