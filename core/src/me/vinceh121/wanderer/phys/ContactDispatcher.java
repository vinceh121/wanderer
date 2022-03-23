package me.vinceh121.wanderer.phys;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.utils.Array;

public class ContactDispatcher extends ContactListener {
	private final Array<IContactListener> listeners = new Array<>();

	public void addContactListener(IContactListener value) {
		listeners.add(value);
	}

	public boolean removeContactListener(IContactListener value) {
		return listeners.removeValue(value, true);
	}

	@Override
	public boolean onContactAdded(btManifoldPoint cp, btCollisionObject colObj0, int partId0, int index0,
			btCollisionObject colObj1, int partId1, int index1) {
		boolean contact = false;
		for (IContactListener l : this.listeners) {
			if (l.onContactAdded(cp, colObj0, partId0, index0, colObj1, partId1, index1)) {
				contact = true;
			}
		}
		return contact;
	}

	@Override
	public void onContactProcessed(btManifoldPoint cp, btCollisionObject colObj0, btCollisionObject colObj1) {
		for (IContactListener l : this.listeners) {
			l.onContactProcessed(cp, colObj0, colObj1);
		}
	}

	@Override
	public void onContactDestroyed(int manifoldPointUserValue) {
		for (IContactListener l : this.listeners) {
			l.onContactDestroyed(manifoldPointUserValue);
		}
	}

	@Override
	public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
		for (IContactListener l : this.listeners)
			l.onContactStarted(colObj0, colObj1);
	}

	@Override
	public void onContactEnded(btCollisionObject colObj0, btCollisionObject colObj1) {
		for (IContactListener l : this.listeners)
			l.onContactEnded(colObj0, colObj1);
	}
}
