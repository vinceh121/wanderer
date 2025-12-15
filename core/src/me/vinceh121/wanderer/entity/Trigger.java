package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.physics.bullet.collision.btGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.phys.IContactListener;

public class Trigger extends AbstractEntity {
	private btGhostObject ghostObject;
	private IContactListener contactListener;

	public Trigger(Wanderer game) {
		super(game);
	}

	public void setRadius(float radius) {
		if (this.ghostObject != null) {
			this.game.getBtWorld().removeCollisionObject(this.ghostObject);
			this.ghostObject.dispose();
		}

		this.ghostObject = new btGhostObject();
		this.ghostObject.setCollisionShape(new btSphereShape(radius));
	}
}
