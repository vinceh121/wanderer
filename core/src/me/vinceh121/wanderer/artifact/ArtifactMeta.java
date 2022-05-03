package me.vinceh121.wanderer.artifact;

import com.badlogic.gdx.audio.Sound;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.character.CharacterW;

public abstract class ArtifactMeta {
	private int energyRequired;
	private boolean red, shrink = true;
	private String artifactModel, artifactTexture;

	public ArtifactMeta(int energyRequired, boolean red, String artifactModel, String artifactTexture) {
		this.energyRequired = energyRequired;
		this.red = red;
		this.artifactModel = artifactModel;
		this.artifactTexture = artifactTexture;
	}

	/**
	 * Tries to pickup the artifact for the player.
	 * 
	 * @return true if the artifact is to be picked up, false otherwise
	 */
	public boolean onPickUp(Wanderer game, CharacterW chara) {
		if (chara.canPickUpArtifact()) {
			WandererConstants.ASSET_MANAGER.get("orig/feedback/artefactcollected.wav", Sound.class).play();
			chara.pickUpArtifact(this);
			return true;
		} else {
			WandererConstants.ASSET_MANAGER.get("orig/feedback/beltfull.wav", Sound.class).play();
			game.showMessage("Belt full!");
			return false;
		}
	}

	public int getEnergyRequired() {
		return energyRequired;
	}

	public void setEnergyRequired(int energyRequired) {
		this.energyRequired = energyRequired;
	}

	public boolean isRed() {
		return red;
	}

	public void setRed(boolean red) {
		this.red = red;
	}

	public String getArtifactModel() {
		return artifactModel;
	}

	public void setArtifactModel(String artifactModel) {
		this.artifactModel = artifactModel;
	}

	public String getArtifactTexture() {
		return artifactTexture;
	}

	public void setArtifactTexture(String artifactTexture) {
		this.artifactTexture = artifactTexture;
	}

	public boolean isShrink() {
		return shrink;
	}

	public void setShrink(boolean shrink) {
		this.shrink = shrink;
	}
}
