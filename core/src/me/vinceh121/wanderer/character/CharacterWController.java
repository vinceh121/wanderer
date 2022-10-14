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
	private float jumpProgress, stepHeight = 0.5f, fallSpeed = 0.2f;
	private Bezier<Vector3> jumpCurve;
	private FallListener fallListener = a -> {
	};

	public CharacterWController(final Wanderer game, final CharacterW character, final float capsuleRadius,
			final float capsuleHeight) {
		this.game = game;
		this.character = character;
		final btCapsuleShape shape = new btCapsuleShape(capsuleRadius, capsuleHeight);
		this.ghostObj = new btPairCachingGhostObject();
		this.ghostObj.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		this.ghostObj.setCollisionShape(shape);
		this.ghostObj.setWorldTransform(character.getTransform().cpy().rotate(Vector3.X, 90));

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
		// starting point, character's translation, add half the height of the capsule
		// to compensate for offset
		points.add(this.character.getTransform()
			.getTranslation(new Vector3())
			.add(0, ((btCapsuleShape) this.ghostObj.getCollisionShape()).getHalfHeight(), 0));
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
		this.fallListener.onFall(this.bigJump);
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
		} else {
			final Vector3 origPos = this.getTranslation();
			this.stepForward();
			this.stepUp();
			this.stepDown();
			if (!this.isTouchingSomething()) {
				this.setWorldTransform(this.getWorldTransform().setTranslation(origPos));
			}
		}

		for (int i = 0; i < 4; i++) {
			if (!this.recoverFromPenetration()) {
				break;
			}
		}
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
			final Vector3 hitPointWorld = new Vector3();
			cb.getHitPointWorld(hitPointWorld);

//			float frac = (origPosition.y - hitPointWorld.y) / 2;
			final Vector3 newPosition = origPosition.cpy();
			newPosition.lerp(end.getTranslation(new Vector3()), cb.getClosestHitFraction());

			this.setWorldTransform(this.getWorldTransform().setTranslation(newPosition));
			this.stopJump0();
			this.falling = true;
		} else {
			this.setWorldTransform(end);
		}

		if (this.jumpProgress >= this.getJumpTime()) {
			// do no call #stopJump() as to not trigger onFall
			this.stopJump0();
			this.falling = true;
		}
		cb.dispose();
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
		final Matrix4 start = new Matrix4();
		final Matrix4 end = new Matrix4();
		final Vector3 origPosition = this.getTranslation();

		final Vector3 downTarget = new Vector3(0, -1, 0).scl(this.fallSpeed);

		final ClosestNotMeConvexResultCallback cb = new ClosestNotMeConvexResultCallback(this.ghostObj,
				this.getTranslation(),
				this.getTranslation().add(downTarget));
		cb.setCollisionFilterGroup(this.ghostObj.getBroadphaseHandle().getCollisionFilterGroup());
		cb.setCollisionFilterMask(this.ghostObj.getBroadphaseHandle().getCollisionFilterMask());
		for (int maxIter = 0; maxIter < 1; maxIter++) {

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
				downTarget.sub(new Vector3(0, fallSpeed, 0));
				continue;
			}
		}

		if (!cb.hasHit()) {
			this.setWorldTransform(this.getWorldTransform().setTranslation(this.getTranslation().add(downTarget)));
			this.falling = true;
			cb.dispose();
			return;
		}
		final Vector3 hitPointWorld = new Vector3();
		cb.getHitPointWorld(hitPointWorld);

		final float frac = (origPosition.y - hitPointWorld.y) / 2;
		if (Float.isNaN(frac)) {
			throw new RuntimeException("NaNaNaNaNaNaNaNaNaNaN BATMAAAAAAAAN");
		}
		final Vector3 newPosition = origPosition.cpy();
		newPosition.lerp(downTarget.add(this.getTranslation()), cb.getClosestHitFraction());

		this.setWorldTransform(this.getWorldTransform().setTranslation(newPosition));
		cb.dispose();

		this.stopJump0();
		this.falling = false;
	}

	private void stepForward() {
		float fraction = 1;

		final Vector3 target = new Vector3();
		this.getWorldTransform().getTranslation(target);
		target.add(this.walkDirection);

		final Matrix4 start = this.getWorldTransform().cpy();
		final Matrix4 end = this.getWorldTransform().cpy();

		int maxIter = 10;

		while (fraction > 0.01f && maxIter-- > 0) {
			start.set(this.getWorldTransform());
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
				cb.dispose();

				final Vector3 direction = new Vector3(target).sub(this.getTranslation());
				final float dist = direction.len();

				final Vector3 newPos = this.getTranslation().cpy().lerp(target, fraction);
				this.setWorldTransform(this.getWorldTransform().setTranslation(newPos));

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
			}
		}
	}

	private void stepUp() {
		final Vector3 up = new Vector3(0, this.stepHeight + 1, 0);

		final Matrix4 end = this.getWorldTransform().cpy();
		end.setTranslation(end.getTranslation(new Vector3()).add(up));

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
				newPos.add(up.cpy().scl(cb.getClosestHitFraction()));
				this.ghostObj.setWorldTransform(this.ghostObj.getWorldTransform().setTranslation(newPos));
			}
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
		super.dispose();
	}

	public interface FallListener {
		void onFall(boolean bigJump);
	}
}