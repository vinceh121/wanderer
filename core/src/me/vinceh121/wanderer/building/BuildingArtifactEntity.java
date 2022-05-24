package me.vinceh121.wanderer.building;

import com.badlogic.gdx.audio.Sound;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.artifact.AbstractArtifactEntity;
import me.vinceh121.wanderer.character.CharacterW;

public class BuildingArtifactEntity extends AbstractArtifactEntity {

	public BuildingArtifactEntity(Wanderer game, BuildingArtifactMeta meta) {
		super(game, meta);

		if (meta.isShrink()) {
			this.scale(0.05f, 0.05f, 0.05f);
		}
	}

	@Override
	public boolean onPickUp(Wanderer game, CharacterW chara) {
		if (chara.canPickUpArtifact()) {
			WandererConstants.ASSET_MANAGER.get("orig/feedback/artefactcollected.wav", Sound.class).play();
			chara.pickUpArtifact((BuildingArtifactMeta) this.getMeta());
			return true;
		} else {
			WandererConstants.ASSET_MANAGER.get("orig/feedback/beltfull.wav", Sound.class).play();
			game.showMessage("Belt full!");
			return false;
		}
	}
}
