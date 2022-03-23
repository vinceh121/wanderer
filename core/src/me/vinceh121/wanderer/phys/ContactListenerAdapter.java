package me.vinceh121.wanderer.phys;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;

public class ContactListenerAdapter implements IContactListener {

	@Override
	public void onContactEnded(btCollisionObject colObj0, btCollisionObject colObj1) {
	}

	@Override
	public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
	}

	@Override
	public void onContactDestroyed(int manifoldPointUserValue) {
	}

	@Override
	public void onContactProcessed(btManifoldPoint cp, btCollisionObject colObj0, btCollisionObject colObj1) {
	}

	@Override
	public boolean onContactAdded(btManifoldPoint cp, btCollisionObject colObj0, int partId0, int index0,
			btCollisionObject colObj1, int partId1, int index1) {
		return false;
	}
}
