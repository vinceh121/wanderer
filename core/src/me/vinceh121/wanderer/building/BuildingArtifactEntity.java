package me.vinceh121.wanderer.building;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.artifact.AbstractArtifactEntity;
import me.vinceh121.wanderer.character.CharacterW;

public class BuildingArtifactEntity extends AbstractArtifactEntity {

	public BuildingArtifactEntity(Wanderer game, BuildingArtifactMeta meta) {
		super(game, setMetaColor(meta));

		if (meta.isShrink()) {
			this.scale(0.05f, 0.05f, 0.05f);
		}
	}
	
	private static BuildingArtifactMeta setMetaColor(BuildingArtifactMeta meta) { // FIXME messy
		if (meta.isRed())
			meta.setArtifactColor(new Color(1f, 0.1f, 0f, 0f));
		return meta;
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
