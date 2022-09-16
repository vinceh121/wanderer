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
			this.stepUp();
			this.stepForward();
			this.stepDown();
		}

		for (int i = 0; i < 4; i++) {
			if (!this.recoverFromPenetration()) {
				break;
			}
		}
	}

	private void stepJump(float delta) {
		this.jumpProgress += delta;
		final Vector3 origPosition = this.getTranslation();
		final Matrix4 start = this.character.getTransform().cpy();
		final Matrix4 end = this.character.getTransform().cpy();
		end.setTranslation(this.jumpCurve.valueAt(new Vector3(), jumpProgress / getJumpTime()));

		final ClosestNotMeConvexResultCallback cb = new ClosestNotMeConvexResultCallback(this.ghostObj,
				start.getTranslation(new Vector3()),
				end.getTranslation(new Vector3()));
		cb.setCollisionFilterGroup(this.ghostObj.getBroadphaseHandle().getCollisionFilterGroup());
		cb.setCollisionFilterMask(this.ghostObj.getBroadphaseHandle().getCollisionFilterMask());

		this.ghostObj.convexSweepTest((btConvexShape) this.ghostObj.getCollisionShape(),
				start,
				end,
				cb,
				this.game.getPhysicsManager().getBtWorld().getDispatchInfo().getAllowedCcdPenetration());
		if (cb.hasHit()) {
			Vector3 hitPointWorld = new Vector3();
			cb.getHitPointWorld(hitPointWorld);

//			float frac = (origPosition.y - hitPointWorld.y) / 2;
			Vector3 newPosition = origPosition.cpy();
			newPosition.lerp(end.getTranslation(new Vector3()), cb.getClosestHitFraction());

			this.setWorldTransform(getWorldTransform().setTranslation(newPosition));
			this.stopJump0();
			this.falling = true;
		} else {
			this.setWorldTransform(end);
		}

		if (this.jumpProgress >= getJumpTime()) {
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
		Matrix4 start = new Matrix4();
		Matrix4 end = new Matrix4();
		Vector3 origPosition = this.getTranslation();

		Vector3 downTarget = new Vector3(0, -1, 0).scl(fallSpeed);

		final ClosestNotMeConvexResultCallback cb = new ClosestNotMeConvexResultCallback(this.ghostObj,
				getTranslation(),
				getTranslation().add(downTarget));
		cb.setCollisionFilterGroup(this.ghostObj.getBroadphaseHandle().getCollisionFilterGroup());
		cb.setCollisionFilterMask(this.ghostObj.getBroadphaseHandle().getCollisionFilterMask());
		for (int maxIter = 0; maxIter < 1; maxIter++) {

			start.idt();
			end.idt();

			start.setTranslation(getTranslation());
			end.setTranslation(getTranslation().add(downTarget));

			this.ghostObj.convexSweepTest((btConvexShape) this.ghostObj.getCollisionShape(),
					start,
					end,
					cb,
					this.game.getPhysicsManager().getBtWorld().getDispatchInfo().getAllowedCcdPenetration());

			if (cb.hasHit() && this.ghostObj.hasContactResponse()
					&& this.validInteract(ghostObj, cb.getHitCollisionObject())) {
				break;
			} else {
//				downTarget.sub(new Vector3(0, fallSpeed, 0));
				continue;
			}
		}

		if (!cb.hasHit()) {
			this.setWorldTransform(getWorldTransform().setTranslation(getTranslation().add(downTarget)));
			this.falling = true;
			cb.dispose();
			return;
		}
		Vector3 hitPointWorld = new Vector3();
		cb.getHitPointWorld(hitPointWorld);

		float frac = (origPosition.y - hitPointWorld.y) / 2;
		if (Float.isNaN(frac))
			throw new RuntimeException("NaNaNaNaNaNaNaNaNaNaN BATMAAAAAAAAN");
		Vector3 newPosition = origPosition.cpy();
		newPosition.lerp(downTarget.add(getTranslation()), cb.getClosestHitFraction());

		this.setWorldTransform(getWorldTransform().setTranslation(newPosition));
		cb.dispose();

		this.stopJump0();
		this.falling = false;
	}

	private void stepForward() {
		float fraction = 1;

		Vector3 target = new Vector3();
		this.getWorldTransform().getTranslation(target);
		target.add(this.walkDirection);

		Matrix4 start = new Matrix4();
		Matrix4 end = new Matrix4();

		int maxIter = 10;

		while (fraction > 0.01f && maxIter-- > 0) {
			start.set(this.getWorldTransform());
			end.setTranslation(target);

			final ClosestNotMeConvexResultCallback cb = new ClosestNotMeConvexResultCallback(ghostObj,
					new Vector3(),
					new Vector3());
			cb.setCollisionFilterGroup(this.ghostObj.getBroadphaseHandle().getCollisionFilterGroup());
			cb.setCollisionFilterMask(this.ghostObj.getBroadphaseHandle().getCollisionFilterMask());

			if (!start.equals(end)) {
				this.ghostObj.convexSweepTest((btConvexShape) this.ghostObj.getCollisionShape(),
						start,
						end,
						cb,
						this.game.getPhysicsManager().getBtWorld().getDispatchInfo().getAllowedCcdPenetration());
			}

			fraction -= cb.getClosestHitFraction();

			if (cb.hasHit() && this.ghostObj.hasContactResponse()
					&& this.validInteract(ghostObj, cb.getHitCollisionObject())) {
				cb.dispose();

				Vector3 direction = new Vector3(target).sub(this.getTranslation());
				float dist = direction.len();

				target.set(getTranslation());

				if (dist > 0.1f) {
					direction.nor();
					Vector3 normalizedTargetDirection = new Vector3(this.walkDirection).nor();
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
		final ClosestNotMeConvexResultCallback cb = new ClosestNotMeConvexResultCallback(ghostObj,
				new Vector3(),
				new Vector3());
		cb.setCollisionFilterGroup(this.ghostObj.getBroadphaseHandle().getCollisionFilterGroup());
		cb.setCollisionFilterMask(this.ghostObj.getBroadphaseHandle().getCollisionFilterMask());

		final Vector3 up = new Vector3(0, 0, this.stepHeight + 1);

		final Matrix4 end = getWorldTransform().cpy();
		end.setTranslation(end.getTranslation(new Vector3()).add(up));

		this.ghostObj.convexSweepTest((btConvexShape) this.ghostObj.getCollisionShape(),
				getWorldTransform(),
				end,
				cb,
				jumpProgress);

		if (cb.hasHit() && this.ghostObj.hasContactResponse()
				&& this.validInteract(ghostObj, cb.getHitCollisionObject())) {
			final Vector3 norm = new Vector3();
			cb.getHitNormalWorld(norm);
			if (norm.dot(Vector3.Z) > 0) {
				Vector3 newPos = new Vector3();
				this.getWorldTransform().getTranslation(newPos);
				newPos.add(up.cpy().scl(cb.getClosestHitFraction()));
				this.ghostObj.setWorldTransform(this.ghostObj.getWorldTransform().setTranslation(newPos));
			}
		}
		cb.dispose();
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
			btBroadphasePair pair = this.ghostObj.getOverlappingPairCache().getOverlappingPairArray().at(i);

			// uhoh, pointers
			btCollisionObject obj0 = btCollisionObject.getInstance(pair.getPProxy0().getClientObject());
			btCollisionObject obj1 = btCollisionObject.getInstance(pair.getPProxy1().getClientObject());

			if ((obj0 != null && !obj0.hasContactResponse()) || (obj1 != null && !obj1.hasContactResponse())) {
				continue;
			}

			if (pair.getAlgorithm() != null)
				pair.getAlgorithm().getAllContactManifolds(manifolds);

			for (int j = 0; j < manifolds.size(); j++) {
				btPersistentManifold m = manifolds.atConst(j);
				for (int k = 0; k < m.getNumContacts(); k++) {
					btManifoldPoint cp = m.getContactPoint(k);
					if (this.recoverFromPenetration(cp, obj0, obj1)) {
						hasPenetration = true;
					}
				}
			}
		}
		manifolds.dispose();
		return hasPenetration;
	}

	public boolean recoverFromPenetration(btManifoldPoint cp, btCollisionObject colObj0, btCollisionObject colObj1) {
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