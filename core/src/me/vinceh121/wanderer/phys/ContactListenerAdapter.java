package me.vinceh121.wanderer.phys;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;

public class ContactListenerAdapter implements IContactListener {

	@Override
	public void onContactEnded(final btCollisionObject colObj0, final btCollisionObject colObj1) {
	}

	@Override
	public void onContactStarted(final btCollisionObject colObj0, final btCollisionObject colObj1) {
	}

	@Override
	public void onContactDestroyed(final int manifoldPointUserValue) {
	}

	@Override
	public void onContactProcessed(final btManifoldPoint cp, final btCollisionObject colObj0,
			final btCollisionObject colObj1) {
	}

	@Override
	public boolean onContactAdded(final btManifoldPoint cp, final btCollisionObject colObj0, final int partId0,
			final int index0, final btCollisionObject colObj1, final int partId1, final int index1) {
		return false;
	}
}
