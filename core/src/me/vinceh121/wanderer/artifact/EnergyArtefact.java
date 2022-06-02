package me.vinceh121.wanderer.artifact;

import com.badlogic.gdx.audio.Sound;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.clan.Clan;

public class EnergyArtefact extends AbstractArtifactEntity {
	private static final ArtifactMeta ENERGY_META = new ArtifactMeta();

	public EnergyArtefact(final Wanderer game) {
		super(game, EnergyArtefact.ENERGY_META);
	}

	@Override
	public boolean onPickUp(final Wanderer game, final CharacterW chara) {
		final Clan c = game.getClanForMember(chara);
		if (c != null) {
			WandererConstants.ASSET_MANAGER.get("orig/feedback/artefactcollected.wav", Sound.class).play();
			// TODO find proper value
			c.setEnergy(Math.min(c.getEnergy() + 10, c.getMaxEnergy()));
			return true;
		} else {
			return false;
		}
	}

	static {
		EnergyArtefact.ENERGY_META.setArtifactModel("orig/energyclot.n/kugel2.obj");
		EnergyArtefact.ENERGY_META.setArtifactModel("orig/lib/eff_glitter/texturenone.ktx");
	}
}
