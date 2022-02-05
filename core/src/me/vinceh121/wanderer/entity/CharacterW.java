package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btActionInterface;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;

import me.vinceh121.wanderer.Wanderer;

/**
 * An entity of a character, a person.
 *
 * Named like that to differentiate with java.lang.Character
 */
public class CharacterW extends AbstractLivingEntity implements IControllableEntity {
	private final btKinematicCharacterController controller;
	private final btPairCachingGhostObject ghostObj;
	private final Vector3 characterDirection = new Vector3();
	private final Vector3 walkDirection = new Vector3();

	public CharacterW(Wanderer game) {
		this(game, 0.3f, 1.5f);
	}

	public CharacterW(Wanderer game, float capsuleRadius, float capsuleHeight) {
		super(game);
//		this.setCollideObject(
//				new btRigidBody(1, createMotionState(), new btCapsuleShape(capsuleRadius, capsuleHeight)));
//		this.getCollideObject().setAngularFactor(1);
//		this.setCollideObjectOffset(new Vector3(0, 0.8f, 0));
		this.ghostObj = new btPairCachingGhostObject();
		this.ghostObj.setCollisionShape(new btCapsuleShape(capsuleRadius, capsuleHeight));
		this.ghostObj.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		game.getBtWorld().addCollisionObject(ghostObj, (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
				(short) (btBroadphaseProxy.CollisionFilterGroups.StaticFilter
						| btBroadphaseProxy.CollisionFilterGroups.DefaultFilter));
		this.controller = new btKinematicCharacterController(ghostObj,
				(btConvexShape) new btCapsuleShape(capsuleRadius, capsuleHeight), 0.35f, Vector3.Y);
		game.getBtWorld().addAction(this.controller);
		this.ghostObj.setFriction(100);
	}

	@Override
	public void updatePhysics(btDiscreteDynamicsWorld world) {
		super.updatePhysics(world);

		Matrix4 colTransform = ghostObj.getWorldTransform();
		Vector3 colTranslation = new Vector3();
		colTransform.getTranslation(colTranslation);
		colTranslation.add(getCollideObjectOffset());
		colTransform.setTranslation(colTranslation);

		this.setTransform(colTransform);
		
		this.processInput();
	}

	@Override
	protected void updateTransform() {
//		super.updateTransform();
		this.ghostObj.setWorldTransform(getTransform());
	}

	@Override
	public void onDeath() {
		// TODO
		System.out.println("Dead!");
	}

	public void processInput() {
		
		if (Gdx.input.isKeyPressed(Keys.SPACE) && controller.canJump()) {
			controller.jump();
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			getTransform().rotate(0, 1, 0, 5f);
			ghostObj.setWorldTransform(getTransform());
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			getTransform().rotate(0, 1, 0, -5f);
			ghostObj.setWorldTransform(getTransform());
		}
		characterDirection.set(-1, 0, 0).rot(getTransform()).nor();
		walkDirection.set(0, 0, 0);

		if (Gdx.input.isKeyPressed(Keys.UP)) {
			walkDirection.add(characterDirection);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			walkDirection.add(-characterDirection.x, -characterDirection.y, -characterDirection.z);
		}
		walkDirection.scl(4f * Gdx.graphics.getDeltaTime());
		controller.setWalkDirection(walkDirection);

	}

	@Override
	public InputProcessor getInputProcessor() {
		return new InputAdapter();
	}

	@Override
	public btActionInterface getBulletAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMass(float mass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onTakeControl() {
//		game.getBtWorld().addAction(this.controller);
	}

	@Override
	public void onRemoveControl() {
//		game.getBtWorld().removeAction(this.controller);
	}
}
