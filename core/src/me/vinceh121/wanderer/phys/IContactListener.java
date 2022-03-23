package me.vinceh121.wanderer.phys;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;

public interface IContactListener {

	void onContactEnded(btCollisionObject colObj0, btCollisionObject colObj1);

	void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1);

	void onContactDestroyed(int manifoldPointUserValue);

	void onContactProcessed(btManifoldPoint cp, btCollisionObject colObj0, btCollisionObject colObj1);

	boolean onContactAdded(btManifoldPoint cp, btCollisionObject colObj0, int partId0, int index0,
			btCollisionObject colObj1, int partId1, int index1);
}
