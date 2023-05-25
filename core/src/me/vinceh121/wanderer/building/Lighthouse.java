package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.combat.DamageType;

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
		return "Lighthouse";
	}
}
