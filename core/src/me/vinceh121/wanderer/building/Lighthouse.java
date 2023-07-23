package me.vinceh121.wanderer.building;

import static me.vinceh121.wanderer.i18n.I18N.gettext;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.combat.DamageType;
import me.vinceh121.wanderer.i18n.I18N;

public class Lighthouse extends AbstractBuilding {
	public Lighthouse(final Wanderer game, final LighthouseMeta meta) {
		super(game, meta);
	}

	@Override
	public void onDeath() {
		super.onDeath();
		this.getIsland().damage(Float.MAX_VALUE, DamageType.EXPLOSION);
	}

	@Override
	public String getName() {
		return I18N.gettext("Lighthouse");
	}
}
