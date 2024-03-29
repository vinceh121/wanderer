package me.vinceh121.wanderer.phys;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.utils.Array;

public class ContactDispatcher extends ContactListener {
	private final Array<IContactListener> listeners = new Array<>();

	public void addContactListener(final IContactListener value) {
		this.listeners.add(value);
	}

	public boolean removeContactListener(final IContactListener value) {
		return this.listeners.removeValue(value, true);
	}

	@Override
	public boolean onContactAdded(final btManifoldPoint cp, final btCollisionObject colObj0, final int partId0,
			final int index0, final btCollisionObject colObj1, final int partId1, final int index1) {
		boolean contact = false;
		for (int i = 0; i < this.listeners.size; i++) {
			final IContactListener l = this.listeners.get(i);
			if (l.onContactAdded(cp, colObj0, partId0, index0, colObj1, partId1, index1)) {
				contact = true;
			}
		}
		return contact;
	}

	@Override
	public void onContactProcessed(final btManifoldPoint cp, final btCollisionObject colObj0,
			final btCollisionObject colObj1) {
		for (int i = 0; i < this.listeners.size; i++) {
			final IContactListener l = this.listeners.get(i);
			l.onContactProcessed(cp, colObj0, colObj1);
		}
	}

	@Override
	public void onContactDestroyed(final int manifoldPointUserValue) {
		for (int i = 0; i < this.listeners.size; i++) {
			final IContactListener l = this.listeners.get(i);
			l.onContactDestroyed(manifoldPointUserValue);
		}
	}

	@Override
	public void onContactStarted(final btCollisionObject colObj0, final btCollisionObject colObj1) {
		for (int i = 0; i < this.listeners.size; i++) {
			final IContactListener l = this.listeners.get(i);
			l.onContactStarted(colObj0, colObj1);
		}
	}

	@Override
	public void onContactEnded(final btCollisionObject colObj0, final btCollisionObject colObj1) {
		for (int i = 0; i < this.listeners.size; i++) {
			final IContactListener l = this.listeners.get(i);
			l.onContactEnded(colObj0, colObj1);
		}
	}
}
