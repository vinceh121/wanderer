package me.vinceh121.wanderer.artifact;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.ParticleEmitter;
import me.vinceh121.wanderer.platform.audio.Sound3D;

public class EnergyArtefact extends AbstractArtifactEntity {
	private static final ArtifactPrototype ENERGY_PROTOTYPE = new ArtifactPrototype() { // FIXME move to a JSON
		@Override
		public AbstractEntity create(final Wanderer game) {
			return new EnergyArtefact(game);
		}
	};

	public EnergyArtefact(final Wanderer game) {
		super(game, EnergyArtefact.ENERGY_PROTOTYPE);
		this.addParticle(new ParticleEmitter(game.getGraphicsManager().getParticleSystem(), "particles/energyclot.p"));
		this.setRotate(false);
	}

	@Override
	public boolean onPickUp(final Wanderer game, final CharacterW chara) {
		final Clan c = game.getClanForMember(chara);
		if (c != null) {
			WandererConstants.ASSET_MANAGER.get("orig/feedback/artefactcollected.wav", Sound3D.class)
				.playGeneral()
				.setDisposeOnStop(true);
			// TODO find proper value
			c.setEnergy(Math.min(c.getEnergy() + 10, c.getMaxEnergy()));
			return true;
		} else {
			return false;
		}
	}

	static {
		EnergyArtefact.ENERGY_PROTOTYPE.setArtifactModel("orig/energyclot.n/kugel2.obj");
		EnergyArtefact.ENERGY_PROTOTYPE.setArtifactModel("orig/lib/eff_glitter/texturenone.ktx");
	}
}
