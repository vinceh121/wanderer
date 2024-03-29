package me.vinceh121.wanderer.character;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeConvexResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btBroadphasePair;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifoldArray;
import com.badlogic.gdx.physics.bullet.dynamics.CustomActionInterface;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;

public class CharacterWController extends CustomActionInterface {
	private final btPairCachingGhostObject ghostObj;
	private final Vector3 walkDirection = new Vector3();
	private final Wanderer game;
	private final CharacterW character;
	private boolean jumping, bigJump, falling;
	private float jumpProgress, stepHeight = 0.5f, fallSpeed = 0.2f, fallTimeDeath = 3f, fallTime;
	private Bezier<Vector3> jumpCurve;
	private FallListener fallListener = new FallListener() {
		@Override
		public void onStartFall() {
		}

		@Override
		public void onJumpEnd(final boolean bigJump) {
		}

		@Override
		public void onEndFall() {
		}

		@Override
		public void shouldDie() {
		}
	};

	public CharacterWController(final Wanderer game, final CharacterW character, final float capsuleRadius,
			final float capsuleHeight) {
		this.game = game;
		this.character = character;
		final btCapsuleShape shape = new btCapsuleShape(capsuleRadius, capsuleHeight);
		this.ghostObj = new btPairCachingGhostObject();
		this.ghostObj.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		this.ghostObj.setCollisionShape(shape);
		this.ghostObj.setWorldTransform(this.character.getTransform().cpy().rotate(Vector3.X, 90));

		// do NOT add this action to the world
		this.game.getBtWorld()
			.addCollisionObject(this.ghostObj,
					CollisionFilterGroups.CharacterFilter,
					CollisionFilterGroups.StaticFilter | CollisionFilterGroups.DefaultFilter
							| CollisionFilterGroups.SensorTrigger);
	}

	public void bigJump() {
		this.jump(2.4f, 15f);
		this.bigJump = true;
	}

	public void jump() {
		this.jump(1.6f, 3f);
	}

	public void jump(final float height, final float distance) {
		if (!this.canJump()) {
			return;
		}
		final Array<Vector3> points = new Array<>(3);
		// starting point, character's translation
		points.add(this.getTranslation());
		// high point, start with relative direction, rotate by global transform, add
		// position offset
		points.add(new Vector3(0, height, distance / 2).rot(this.getWorldTransform()).add(points.peek()));
		// down fall, like this entire codebase
		points.add(new Vector3(0, 0, distance).rot(this.getWorldTransform()).add(points.first()));
		this.jumpCurve = new Bezier<>(points, 0, points.size);
		this.jumping = true;
	}

	public boolean canJump() {
		return !this.falling && !this.jumping;
	}

	public void stopJump() {
		if (!this.jumping) {
			return;
		}
		this.stopJump0();
		this.fallListener.onJumpEnd(this.bigJump);
	}

	private void stopJump0() {
		this.jumping = false;
		this.bigJump = false;
		this.jumpProgress = 0;
	}

	@Override
	public void updateAction(final float deltaTimeStep) {
		if (this.jumping) {
			this.stepJump(deltaTimeStep);
		} else if (this.falling) {
			this.walkDirection.setZero();
			this.stepDown();
			this.checkFallDeath(deltaTimeStep);
		} else {
			final Vector3 origPos = this.getTranslation();
			this.stepUp();
			this.stepForward(deltaTimeStep);
			this.stepDown();
			if (!this.isTouchingSomething()) {
				this.setWorldTransform(this.getWorldTransform().setTranslation(origPos));
			}
		}

//		for (int i = 0; i < 4; i++) {
//			if (!this.recoverFromPenetration()) {
//				break;
//			}
//		}
	}

	private void checkFallDeath(final float delta) {
		this.fallTime += delta;
		if (this.fallTime < this.fallTimeDeath) {
			return;
		}

		// perform test to know if we can still land on something
		final Matrix4 start = this.getWorldTransform().cpy();
		final Matrix4 end = this.getWorldTransform().cpy();
		end.trn(0, -100, 0);

		final ClosestNotMeConvexResultCallback cb = new ClosestNotMeConvexResultCallback(this.ghostObj,
				start.getTranslation(new Vector3()),
				end.getTranslation(new Vector3()));
		cb.setCollisionFilterGroup(this.ghostObj.getBroadphaseHandle().getCollisionFilterGroup());
		cb.setCollisionFilterMask(this.ghostObj.getBroadphaseHandle().getCollisionFilterMask());
		if (!cb.hasHit()) {
			this.fallListener.shouldDie();
		}
		cb.dispose();
	}

	private void stepJump(final float delta) {
		this.jumpProgress += delta;
		final Vector3 origPosition = this.getTranslation();
		final Matrix4 start = this.getWorldTransform().cpy();
		final Matrix4 end = this.getWorldTransform().cpy();
		end.setTranslation(this.jumpCurve.valueAt(new Vector3(), this.jumpProgress));

		final ClosestNotMeConvexResultCallback cb = new ClosestNotMeConvexResultCallback(this.ghostObj,
				start.getTranslation(new Vector3()),
				end.getTranslation(new Vector3()));
		cb.setCollisionFilterGroup(this.ghostObj.getBroadphaseHandle().getCollisionFilterGroup());
		cb.setCollisionFilterMask(this.ghostObj.getBroadphaseHandle().getCollisionFilterMask());

		this.ghostObj.convexSweepTest((btConvexShape) this.ghostObj.getCollisionShape(),
				start,
				end,
				cb,
				this.getCCDPenetration());
		if (cb.hasHit()) {
			final Vector3 newPosition = origPosition.cpy();
			newPosition.lerp(end.getTranslation(new Vector3()), cb.getClosestHitFraction());

			this.setWorldTransform(this.getWorldTransform().setTranslation(newPosition));
			this.stopJump();
			this.startFalling();
		} else {
			this.setWorldTransform(end);
		}

		if (this.jumpProgress >= this.getJumpTime()) {
			// do no call #stopJump() as to not trigger onFall
			this.stopJump0();
			this.startFalling();
		}
		cb.dispose();
	}

	private void startFalling() {
		if (!this.falling) {
			this.falling = true;
			this.fallTime = 0;
		}
	}

	/**
	 * @return the max time of the current's jump in seconds, whether big or small
	 */
	private float getJumpTime() {
		if (this.bigJump) {
			return 3f;
		} else {
			return 1.2f;
		}
	}

	private void stepDown() {
		final int maxIter = 10;

		final Matrix4 start = new Matrix4();
		final Matrix4 end = new Matrix4();
		final Vector3 origPosition = this.getTranslation();

		final Vector3 downTarget = new Vector3(0, -1, 0).scl(this.fallSpeed);

		final ClosestNotMeConvexResultCallback cb = new ClosestNotMeConvexResultCallback(this.ghostObj,
				this.getTranslation(),
				this.getTranslation().add(downTarget));
		cb.setCollisionFilterGroup(this.ghostObj.getBroadphaseHandle().getCollisionFilterGroup());
		cb.setCollisionFilterMask(this.ghostObj.getBroadphaseHandle().getCollisionFilterMask());
		for (int iter = 0; iter < maxIter; iter++) {
			start.idt();
			end.idt();

			start.setTranslation(this.getTranslation());
			end.setTranslation(this.getTranslation().add(downTarget));

			this.ghostObj.convexSweepTest((btConvexShape) this.ghostObj.getCollisionShape(),
					start,
					end,
					cb,
					this.getCCDPenetration());

			if (cb.hasHit() && this.ghostObj.hasContactResponse()
					&& this.validInteract(this.ghostObj, cb.getHitCollisionObject())) {
				break;
			} else {
				downTarget.sub(new Vector3(0, this.fallSpeed, 0));
				continue;
			}
		}

		if (!cb.hasHit()) {
			this.setWorldTransform(this.getWorldTransform()
				.setTranslation(this.getTranslation().add(new Vector3(0, -1, 0).scl(this.fallSpeed))));
			this.startFalling();
			cb.dispose();
			return;
		}
		final Vector3 hitPointWorld = new Vector3();
		cb.getHitPointWorld(hitPointWorld);

		final Vector3 newPosition = origPosition.cpy();
		newPosition.lerp(downTarget.add(this.getTranslation()), cb.getClosestHitFraction());

		this.setWorldTransform(this.getWorldTransform().setTranslation(newPosition));
		cb.dispose();

		this.stopJump0();
		this.falling = false;
	}

	private void stepForward(final float delta) {
		float fraction = 1;

		final Vector3 target = new Vector3();
		this.getWorldTransform().getTranslation(target);
		target.add(this.walkDirection.cpy().scl(delta / (1f / 60f)));

		final Matrix4 start = this.getWorldTransform().cpy();
		final Matrix4 end = this.getWorldTransform().cpy();

		int maxIter = 10;

		while (fraction > 0.01f && maxIter-- > 0) {
			end.setTranslation(target);

			final ClosestNotMeConvexResultCallback cb = new ClosestNotMeConvexResultCallback(this.ghostObj,
					start.getTranslation(new Vector3()),
					end.getTranslation(new Vector3()));
			cb.setCollisionFilterGroup(this.ghostObj.getBroadphaseHandle().getCollisionFilterGroup());
			cb.setCollisionFilterMask(this.ghostObj.getBroadphaseHandle().getCollisionFilterMask());

			this.ghostObj.convexSweepTest((btConvexShape) this.ghostObj.getCollisionShape(),
					start,
					end,
					cb,
					this.getCCDPenetration());

			fraction -= cb.getClosestHitFraction();

			if (cb.hasHit() && this.ghostObj.hasContactResponse()
					&& this.validInteract(this.ghostObj, cb.getHitCollisionObject())) {
				final Vector3 hitNormal = new Vector3();
				cb.getHitNormalWorld(hitNormal);
				cb.dispose();

				final Vector3 direction = new Vector3(target).sub(this.getTranslation());
				final float dist = direction.len();

				if (dist >= 0) {
					direction.nor();
					final Vector3 reflectDir =
							direction.cpy().sub(hitNormal.cpy().scl(direction.dot(hitNormal) * 2)).nor();
					final Vector3 perpendicularDir =
							reflectDir.cpy().sub(hitNormal.cpy().scl(reflectDir.dot(hitNormal)));
					final Vector3 perpendicularComponent = reflectDir.cpy().sub(perpendicularDir);
					this.setWorldTransform(
							this.getWorldTransform().setTranslation(this.getTranslation().add(perpendicularComponent)));
				}

				if (dist > 0.1f) {
					direction.nor();
					final Vector3 normalizedTargetDirection = new Vector3(this.walkDirection).nor();
					if (direction.dot(normalizedTargetDirection) <= 0) {
						break;
					}
				} else {
					break;
				}
			} else {
				cb.dispose();
				this.setWorldTransform(this.getWorldTransform().setTranslation(target));
				break;
			}
		}
	}

	private void stepUp() {
		final Vector3 up = new Vector3(0, this.stepHeight + 1, 0);

		final Matrix4 end = this.getWorldTransform().cpy();
		final Vector3 target = end.getTranslation(new Vector3()).add(up);
		end.setTranslation(target);

		final ClosestNotMeConvexResultCallback cb = new ClosestNotMeConvexResultCallback(this.ghostObj,
				this.getTranslation(),
				end.getTranslation(new Vector3()));
		cb.setCollisionFilterGroup(this.ghostObj.getBroadphaseHandle().getCollisionFilterGroup());
		cb.setCollisionFilterMask(this.ghostObj.getBroadphaseHandle().getCollisionFilterMask());

		this.ghostObj.convexSweepTest((btConvexShape) this.ghostObj.getCollisionShape(),
				this.getWorldTransform(),
				end,
				cb,
				this.getCCDPenetration());

		if (cb.hasHit() && this.ghostObj.hasContactResponse()
				&& this.validInteract(this.ghostObj, cb.getHitCollisionObject())) {
			final Vector3 norm = new Vector3();
			cb.getHitNormalWorld(norm);
			if (norm.dot(Vector3.Z) > 0) {
				final Vector3 newPos = new Vector3();
				this.getWorldTransform().getTranslation(newPos);
				newPos.lerp(newPos, cb.getClosestHitFraction());
				this.ghostObj.setWorldTransform(this.ghostObj.getWorldTransform().setTranslation(newPos));
			}
		} else {
			this.setWorldTransform(end);
		}
		cb.dispose();
	}

	public boolean isTouchingSomething() {
		final btPersistentManifoldArray manifolds = new btPersistentManifoldArray();
		this.game.getBtWorld()
			.getDispatcher()
			.dispatchAllCollisionPairs(this.ghostObj.getOverlappingPairCache(),
					this.game.getBtWorld().getDispatchInfo(),
					this.game.getBtWorld().getDispatcher());
		for (int i = 0; i < this.ghostObj.getOverlappingPairCache().getNumOverlappingPairs(); i++) {
			final btBroadphasePair pair = this.ghostObj.getOverlappingPairCache().getOverlappingPairArray().at(i);

			// uhoh, pointers
			final btCollisionObject obj0 = btCollisionObject.getInstance(pair.getPProxy0().getClientObject());
			final btCollisionObject obj1 = btCollisionObject.getInstance(pair.getPProxy1().getClientObject());

			if (obj0 != null && !obj0.hasContactResponse() || obj1 != null && !obj1.hasContactResponse()) {
				return false;
			}

			if (pair.getAlgorithm() != null) {
				pair.getAlgorithm().getAllContactManifolds(manifolds);
			}

			for (int j = 0; j < manifolds.size(); j++) {
				final btPersistentManifold m = manifolds.atConst(j);
				if (m.getNumContacts() > 0) {
					manifolds.dispose();
					return true;
				}
			}
		}
		manifolds.dispose();
		return false;
	}

	public boolean recoverFromPenetration() {
		final btPersistentManifoldArray manifolds = new btPersistentManifoldArray();
		boolean hasPenetration = false;
		this.game.getBtWorld()
			.getDispatcher()
			.dispatchAllCollisionPairs(this.ghostObj.getOverlappingPairCache(),
					this.game.getBtWorld().getDispatchInfo(),
					this.game.getBtWorld().getDispatcher());

		for (int i = 0; i < this.ghostObj.getOverlappingPairCache().getNumOverlappingPairs(); i++) {
			final btBroadphasePair pair = this.ghostObj.getOverlappingPairCache().getOverlappingPairArray().at(i);

			// uhoh, pointers
			final btCollisionObject obj0 = btCollisionObject.getInstance(pair.getPProxy0().getClientObject());
			final btCollisionObject obj1 = btCollisionObject.getInstance(pair.getPProxy1().getClientObject());

			if (obj0 != null && !obj0.hasContactResponse() || obj1 != null && !obj1.hasContactResponse()) {
				continue;
			}

			if (pair.getAlgorithm() != null) {
				pair.getAlgorithm().getAllContactManifolds(manifolds);
			}

			for (int j = 0; j < manifolds.size(); j++) {
				final btPersistentManifold m = manifolds.atConst(j);
				for (int k = 0; k < m.getNumContacts(); k++) {
					final btManifoldPoint cp = m.getContactPoint(k);
					if (this.recoverFromPenetration(cp, obj0, obj1)) {
						hasPenetration = true;
					}
				}
			}
		}
		manifolds.dispose();
		return hasPenetration;
	}

	public boolean recoverFromPenetration(final btManifoldPoint cp, final btCollisionObject colObj0,
			final btCollisionObject colObj1) {
		// https://github.com/bulletphysics/bullet3/blob/e306b274f1885f32b7e9d65062aa942b398805c2/src/BulletDynamics/Character/btKinematicCharacterController.cpp#L238
		final float dist = cp.getDistance();
		if (this.validInteract(colObj0, colObj1) && dist < -0.25f) {
			final Vector3 pos = CharacterWController.this.ghostObj.getWorldTransform().getTranslation(new Vector3());
			final Vector3 normalWorldB = new Vector3();
			cp.getNormalWorldOnB(normalWorldB);
			pos.add(normalWorldB.scl(colObj0.getCPointer() == CharacterWController.this.ghostObj.getCPointer() ? -1 : 1)
				.scl(dist)
				.scl(0.2f));
			// this weird shit is due to JNI
			CharacterWController.this
				.setWorldTransform(CharacterWController.this.getWorldTransform().setTranslation(pos));
			return true;
		}
		return false;
	}

	/**
	 * @param colObj0
	 * @param colObj1
	 * @return if either obj is this character and the other isn't a sensor
	 */
	private boolean validInteract(final btCollisionObject colObj0, final btCollisionObject colObj1) {
		return colObj0.getCPointer() == CharacterWController.this.ghostObj.getCPointer()
				&& (colObj1.getCollisionFlags() & CollisionFlags.CF_NO_CONTACT_RESPONSE) == 0
				|| colObj1.getCPointer() == CharacterWController.this.ghostObj.getCPointer()
						&& (colObj0.getCollisionFlags() & CollisionFlags.CF_NO_CONTACT_RESPONSE) == 0;
	}

	private float getCCDPenetration() {
		return this.game.getPhysicsManager().getBtWorld().getDispatchInfo().getAllowedCcdPenetration();
	}

	/**
	 * @param walkDirection
	 * @see com.badlogic.gdx.physics.bullet.dynamics.btCharacterControllerInterface#setWalkDirection(com.badlogic.gdx.math.Vector3)
	 */
	public void setWalkDirection(final Vector3 walkDirection) {
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

	public boolean isFalling() {
		return this.falling;
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

	public Vector3 getTranslation() {
		return this.getWorldTransform().getTranslation(new Vector3());
	}

	@Override
	public void debugDraw() {
	}

	/**
	 * @return
	 * @see com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController#getGhostObject()
	 */
	public btPairCachingGhostObject getGhostObject() {
		return this.ghostObj;
	}

	@Override
	public void dispose() {
		this.game.getBtWorld().removeCollisionObject(this.ghostObj);
		this.ghostObj.dispose();
		super.dispose();
	}

	public interface FallListener {
		void onStartFall();

		void onEndFall();

		void onJumpEnd(boolean bigJump);

		void shouldDie();
	}
}