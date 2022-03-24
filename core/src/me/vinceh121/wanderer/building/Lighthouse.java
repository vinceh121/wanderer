package me.vinceh121.wanderer.building;

import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;

import me.vinceh121.wanderer.Wanderer;

public class Lighthouse extends Building {
	public Lighthouse(Wanderer game) {
		super(game);
		this.getInteractZone().setCollisionShape(new btCapsuleShape(11f, 20f));
	}
	
	@Override
	public void onDeath() {
		super.onDeath();
		this.getIsland().damage(Float.MAX_VALUE);
	}
}
