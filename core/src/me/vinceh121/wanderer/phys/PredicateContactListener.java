package me.vinceh121.wanderer.phys;

import java.util.function.BiPredicate;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;

public class PredicateContactListener extends ContactListenerAdapter {
	private final IContactListener delegate;
	private final BiPredicate<btCollisionObject, btCollisionObject> predicate;

	public PredicateContactListener(BiPredicate<btCollisionObject, btCollisionObject> predicate,
			IContactListener delegate) {
		this.delegate = delegate;
		this.predicate = predicate;
	}

	@Override
	public boolean onContactAdded(btManifoldPoint cp, btCollisionObject colObj0, int partId0, int index0,
			btCollisionObject colObj1, int partId1, int index1) {
		if (predicate.test(colObj0, colObj1))
			return delegate.onContactAdded(cp, colObj0, partId0, index0, colObj1, partId1, index1);
		else
			return false;
	}

	@Override
	public void onContactProcessed(btManifoldPoint cp, btCollisionObject colObj0,
			btCollisionObject colObj1) {
		if (predicate.test(colObj0, colObj1))
			delegate.onContactProcessed(cp, colObj0, colObj1);
	}

	@Override
	public void onContactDestroyed(int manifoldPointUserValue) {
		delegate.onContactDestroyed(manifoldPointUserValue);
	}

	@Override
	public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
		if (predicate.test(colObj0, colObj1))
			delegate.onContactStarted(colObj0, colObj1);
	}

	@Override
	public void onContactEnded(btCollisionObject colObj0, btCollisionObject colObj1) {
		if (predicate.test(colObj0, colObj1))
			delegate.onContactEnded(colObj0, colObj1);
	}
}
