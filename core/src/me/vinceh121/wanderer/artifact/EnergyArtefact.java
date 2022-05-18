package me.vinceh121.wanderer.artifact;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.clan.Clan;

public class EnergyArtefact extends ArtifactMeta {

	public EnergyArtefact() {
		super(0, false, "orig/energyclot.n/kugel2.obj", "orig/lib/eff_glitter/texturenone.ktx");
	}

	@Override
	public boolean onPickUp(Wanderer game, CharacterW chara) {
		Clan c = game.getClanForMember(chara);
		if (c != null) {
			c.setEnergy(Math.min(c.getEnergy() + 10, c.getMaxEnergy()));
			return true;
		} else {
			return false;
		}
	}

}
