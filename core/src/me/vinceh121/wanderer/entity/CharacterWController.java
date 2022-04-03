package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.CustomActionInterface;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.phys.ContactListenerAdapter;
import me.vinceh121.wanderer.phys.IContactListener;

public class CharacterWController extends CustomActionInterface {
	private final IContactListener contactListener = new ContactListenerAdapter() {
		@Override
		public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
			// if either obj is this character and the other isn't a sensor
			if ((colObj0.getCPointer() == CharacterWController.this.ghostObj.getCPointer()
					&& ((colObj1.getCollisionFlags() & CollisionFlags.CF_NO_CONTACT_RESPONSE) == 0))
					|| colObj1.getCPointer() == CharacterWController.this.ghostObj.getCPointer()
							&& ((colObj0.getCollisionFlags() & CollisionFlags.CF_NO_CONTACT_RESPONSE) == 0)) {
				CharacterWController.this.stopJump();
			}
		};
	};
	private final btKinematicCharacterController delegateController;
	private final btPairCachingGhostObject ghostObj;
	private final Vector3 walkDirection = new Vector3();
	private final CharacterW character;
	private boolean jumping, bigJump;
	private float jumpProgress;
	private Bezier<Vector3> jumpCurve;
	private FallListener fallListener = a -> {
	};

	public CharacterWController(final CharacterW character) {
		this.character = character;
//		final btCapsuleShape chShape = (btCapsuleShape) character.getCollideObject().getCollisionShape();
//		final btCapsuleShape shape = new btCapsuleShape(chShape.getRadius(), chShape.getHalfHeight() * 2);
		final btCapsuleShape shape = new btCapsuleShape(0.3f, 1.5f);
		this.ghostObj = new btPairCachingGhostObject();
		this.ghostObj.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		this.ghostObj.setCollisionShape(shape);
		this.ghostObj.setWorldTransform(character.getTransform().cpy().rotate(Vector3.X, 90));
		// do NOT add this action to the world
		this.delegateController = new btKinematicCharacterController(this.ghostObj, shape, 0.35f, Vector3.Y);
		this.character.game.getBtWorld().addCollisionObject(this.ghostObj, CollisionFilterGroups.CharacterFilter,
				CollisionFilterGroups.StaticFilter | CollisionFilterGroups.DefaultFilter
						| CollisionFilterGroups.SensorTrigger);
		this.character.game.getPhysicsManager().addContactListener(contactListener);
	}

	public void bigJump() {
		this.jump(4, 8);
		this.bigJump = true;
	}

	public void jump() {
		this.jump(2, 2);
	}

	public void jump(final int height, final int distance) {
		if (this.jumping || !this.delegateController.canJump()) {
			return;
		}
		final Array<Vector3> points = new Array<>(3);
		// starting point, character's translation, add half the height of the capsule
		// to compensate for offset
		points.add(this.character.getTransform().getTranslation(new Vector3()).add(0,
				((btCapsuleShape) this.ghostObj.getCollisionShape()).getHalfHeight(), 0));
		// high point, start with relative direction, rotate by global transform, add
		// position offset
		points.add(new Vector3(0, height, 1).rot(this.getWorldTransform()).add(points.peek()));
		// down fall, like this entire codebase
		points.add(new Vector3(0, 0, distance).rot(this.getWorldTransform()).add(points.first()));
		this.jumpCurve = new Bezier<>(points, 0, points.size);
		this.jumping = true;
	}

	public void stopJump() {
		if (!this.jumping) {
			return;
		}
		this.jumping = false;
		this.bigJump = false;
		this.jumpProgress = 0;
		this.fallListener.onFall(this.bigJump);
	}

	@Override
	public void updateAction(final float deltaTimeStep) {
		if (this.jumping) {
			final Matrix4 trans = this.character.getTransform();
			trans.setTranslation(this.jumpCurve.valueAt(new Vector3(), this.jumpProgress));
			this.ghostObj.setWorldTransform(trans);
			this.character.setTransform(trans);
			this.jumpProgress += deltaTimeStep;
			return;
		}
		this.delegateController.updateAction(this.character.game.getBtWorld(), deltaTimeStep);
	}

	/**
	 * @param walkDirection
	 * @see com.badlogic.gdx.physics.bullet.dynamics.btCharacterControllerInterface#setWalkDirection(com.badlogic.gdx.math.Vector3)
	 */
	public void setWalkDirection(final Vector3 walkDirection) {
		this.delegateController.setWalkDirection(walkDirection);
		this.walkDirection.set(walkDirection);
	}

	/**
	 * @return
	 * @see com.badlogic.gdx.physics.bullet.collision.btCollisionObject#getWorldTransform()
	 */
	public Matrix4 getWorldTransform() {
		return this.ghostObj.getWorldTransform();
	}

	/**
	 * @param worldTrans
	 * @see com.badlogic.gdx.physics.bullet.collision.btCollisionObject#setWorldTransform(com.badlogic.gdx.math.Matrix4)
	 */
	public void setWorldTransform(final Matrix4 worldTrans) {
		this.ghostObj.setWorldTransform(worldTrans);
	}

	public boolean isJumping() {
		return this.jumping;
	}

	public boolean isBigJump() {
		return this.bigJump;
	}

	/**
	 * @return the fallListener
	 */
	public FallListener getFallListener() {
		return this.fallListener;
	}

	/**
	 * @param fallListener the fallListener to set
	 */
	public void setFallListener(final FallListener fallListener) {
		this.fallListener = fallListener;
	}

	@Override
	public void debugDraw() {
	}

	/**
	 * @return
	 * @see com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController#getGhostObject()
	 */
	public btPairCachingGhostObject getGhostObject() {
		return this.delegateController.getGhostObject();
	}

	@Override
	public void dispose() {
		this.character.game.getPhysicsManager().removeContactListener(contactListener);
		this.delegateController.dispose();
		super.dispose();
	}

	public interface FallListener {
		void onFall(boolean bigJump);
	}
}