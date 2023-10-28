package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.artifact.AbstractArtifactEntity;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.i18n.I18N;
import me.vinceh121.wanderer.platform.audio.Sound3D;

public class BuildingArtifactEntity extends AbstractArtifactEntity {

	public BuildingArtifactEntity(final Wanderer game, final AbstractBuildingPrototype prototype) {
		super(game, prototype);

		this.scale(prototype.getArtefactScale());
	}

	@Override
	public boolean onPickUp(final Wanderer game, final CharacterW chara) {
		if (chara.canPickUpArtifact()) {
			WandererConstants.ASSET_MANAGER.get("orig/feedback/artefactcollected.wav", Sound3D.class)
				.playSource3D()
				.setPosition(WandererConstants.AUDIO.getListenerPosition());
			chara.pickUpArtifact(this.getPrototype());
			return true;
		} else {
			WandererConstants.ASSET_MANAGER.get("orig/feedback/beltfull.wav", Sound3D.class).playGeneral();
			game.showMessage(I18N.gettext("Belt full!"));
			return false;
		}
	}
}
