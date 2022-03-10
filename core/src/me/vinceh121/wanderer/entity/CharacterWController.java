package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.CustomActionInterface;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.WandererConstants;

public class CharacterWController extends CustomActionInterface {
	private final ContactListener contactListener = new ContactListener() {
		public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
			if (colObj0.getCPointer() == ghostObj.getCPointer() || colObj1.getCPointer() == ghostObj.getCPointer()) {
				stopJump();
			}
		};
	};
	private final btKinematicCharacterController delegateController;
	private final btPairCachingGhostObject ghostObj;
	private CharacterW character;
	private boolean jumping;
	private float jumpProgress;
	private Bezier<Vector3> jumpCurve;

	public CharacterWController(CharacterW character) {
		this.character = character;
//		final btCapsuleShape chShape = (btCapsuleShape) character.getCollideObject().getCollisionShape();
//		final btCapsuleShape shape = new btCapsuleShape(chShape.getRadius(), chShape.getHalfHeight() * 2);
		final btCapsuleShape shape = new btCapsuleShape(0.3f, 1.5f);
		ghostObj = new btPairCachingGhostObject();
		ghostObj.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		ghostObj.setCollisionShape(shape);
		ghostObj.setWorldTransform(character.getTransform().cpy().rotate(Vector3.X, 90));
		// do NOT add this action to the world
		this.delegateController = new btKinematicCharacterController(ghostObj, shape, 0.35f, Vector3.Y);
		this.character.game.getBtWorld().addCollisionObject(ghostObj,
				(short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
				(short) (btBroadphaseProxy.CollisionFilterGroups.StaticFilter
						| btBroadphaseProxy.CollisionFilterGroups.DefaultFilter));
		this.contactListener.enable();
	}

	public void jump() {
		if (this.jumping)
			return;
		final Array<Vector3> points = new Array<>(3);
		points.add(this.character.getTransform().getTranslation(new Vector3()));
		points.add(points.peek().cpy().add(0, 2, 0));
		points.add(points.first().cpy().add(0, 0, 2));
		this.jumpCurve = new Bezier<>(points, 0, points.size);
		this.jumping = true;
	}

	public void stopJump() {
		if (!this.jumping)
			return;
		this.jumping = false;
		this.jumpProgress = 0;
		WandererConstants.ASSET_MANAGER.get("orig/lib/sound/step_bigland_john.wav", Sound.class).play();
	}

	@Override
	public void updateAction(float deltaTimeStep) {
		if (this.jumping) {
			System.out.println("jumping");
			Matrix4 trans = this.character.getTransform();
			trans.setTranslation(this.jumpCurve.valueAt(new Vector3(), jumpProgress));
			this.ghostObj.setWorldTransform(trans);
			this.character.setTransform(trans);
			jumpProgress += deltaTimeStep;
			return;
		}
		this.delegateController.updateAction(character.game.getBtWorld(), deltaTimeStep);
	}

	/**
	 * @param walkDirection
	 * @see com.badlogic.gdx.physics.bullet.dynamics.btCharacterControllerInterface#setWalkDirection(com.badlogic.gdx.math.Vector3)
	 */
	public void setWalkDirection(Vector3 walkDirection) {
		System.out.println(walkDirection);
		delegateController.setWalkDirection(walkDirection);
	}

	@Override
	public void debugDraw() {
	}

	/**
	 * @return
	 * @see com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController#getGhostObject()
	 */
	public btPairCachingGhostObject getGhostObject() {
		return delegateController.getGhostObject();
	}

	@Override
	public void dispose() {
		this.contactListener.dispose();
		this.delegateController.dispose();
		super.dispose();
	}

	static {
		WandererConstants.ASSET_MANAGER.load("orig/lib/sound/step_bigland_john.wav", Sound.class);
	}
}