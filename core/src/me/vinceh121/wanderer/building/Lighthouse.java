package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.combat.DamageType;
import me.vinceh121.wanderer.i18n.I18N;

public class Lighthouse extends AbstractBuilding {
	public Lighthouse(final Wanderer game, final LighthousePrototype prototype) {
		super(game, prototype);

		this.setControlMessage(/* Popup message when close to building */I18N.gettext("Lighthouse"));
	}

	@Override
	public void onDeath() {
		super.onDeath();
		this.getIsland().damage(Float.MAX_VALUE, DamageType.EXPLOSION);
	}
}
