package me.vinceh121.wanderer.building;

import me.vinceh121.wanderer.Wanderer;

public class Lighthouse extends AbstractBuilding {
	public Lighthouse(Wanderer game, final LighthouseMeta meta) {
		super(game, meta);
	}
	
	@Override
	public void onDeath() {
		super.onDeath();
		this.getIsland().damage(Float.MAX_VALUE);
	}
	
	@Override
	public String getName() {
		return "Lighthouse";
	}
}
