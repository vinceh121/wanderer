package me.vinceh121.wanderer.phys;

import java.util.function.BiPredicate;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;

public class PredicateContactListener extends ContactListenerAdapter {
	private final IContactListener delegate;
	private final BiPredicate<btCollisionObject, btCollisionObject> predicate;

	public PredicateContactListener(final BiPredicate<btCollisionObject, btCollisionObject> predicate,
			final IContactListener delegate) {
		this.delegate = delegate;
		this.predicate = predicate;
	}

	@Override
	public boolean onContactAdded(final btManifoldPoint cp, final btCollisionObject colObj0, final int partId0,
			final int index0, final btCollisionObject colObj1, final int partId1, final int index1) {
		if (this.predicate.test(colObj0, colObj1)) {
			return this.delegate.onContactAdded(cp, colObj0, partId0, index0, colObj1, partId1, index1);
		} else {
			return false;
		}
	}

	@Override
	public void onContactProcessed(final btManifoldPoint cp, final btCollisionObject colObj0,
			final btCollisionObject colObj1) {
		if (this.predicate.test(colObj0, colObj1)) {
			this.delegate.onContactProcessed(cp, colObj0, colObj1);
		}
	}

	@Override
	public void onContactDestroyed(final int manifoldPointUserValue) {
		this.delegate.onContactDestroyed(manifoldPointUserValue);
	}

	@Override
	public void onContactStarted(final btCollisionObject colObj0, final btCollisionObject colObj1) {
		if (this.predicate.test(colObj0, colObj1)) {
			this.delegate.onContactStarted(colObj0, colObj1);
		}
	}

	@Override
	public void onContactEnded(final btCollisionObject colObj0, final btCollisionObject colObj1) {
		if (this.predicate.test(colObj0, colObj1)) {
			this.delegate.onContactEnded(colObj0, colObj1);
		}
	}
}
