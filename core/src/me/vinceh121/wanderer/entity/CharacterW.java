package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;

/**
 * An entity of a character, a person.
 *
 * Named like that to differentiate with java.lang.Character
 */
public class CharacterW extends AbstractLivingControllableEntity {
	private final CharacterWController controller;
	private final Vector3 characterDirection = new Vector3();
	private final Vector3 walkDirection = new Vector3();

	public CharacterW(Wanderer game) {
		this(game, 0.3f, 1.5f);
	}

	public CharacterW(Wanderer game, float capsuleRadius, float capsuleHeight) {
		super(game);
//		this.setCollideObject(new btRigidBody(1, createMotionState(), new btCapsuleShape(capsuleRadius, capsuleHeight)),
//				btBroadphaseProxy.CollisionFilterGroups.DefaultFilter,
//				btBroadphaseProxy.CollisionFilterGroups.StaticFilter);
//		this.getCollideObject().setAngularFactor(1);
//		this.getCollideObject().setFriction(100);
		this.setCollideObjectOffset(new Vector3(0, 0.8f, 0));
		this.controller = new CharacterWController(this);
		this.controller.setFallListener(this::onFall);
		game.getBtWorld().addAction(this.controller);
	}

	private void onFall(boolean bigJump) {
		WandererConstants.ASSET_MANAGER.get("orig/lib/sound/step_bigland_john.wav", Sound.class).play();
	}

	@Override
	public void updatePhysics(btDiscreteDynamicsWorld world) {
		super.updatePhysics(world);

		Matrix4 colTransform = this.controller.getGhostObject().getWorldTransform();
		Vector3 colTranslation = new Vector3();
		colTransform.getTranslation(colTranslation);
		colTranslation.sub(getCollideObjectOffset());
		colTransform.setTranslation(colTranslation);

		// do not call setTransform as not to cause update to ghostobject
		this.getTransform().set(colTransform);
	}

	@Override
	public void render(ModelBatch batch, Environment env) {
		super.render(batch, env);
		if (this.isControlled()) {
			this.processInput();
			this.moveCamera();
		}
	}

	private void moveCamera() {
		PerspectiveCamera cam = this.game.getCamera();
		Vector3 characterTransform = new Vector3();
		getTransform().getTranslation(characterTransform);

		Quaternion characterRotation = new Quaternion();
		getTransform().getRotation(characterRotation);

		cam.position.set(characterRotation.transform(new Vector3(0, 3, -4)).add(characterTransform));
		cam.lookAt(characterTransform.cpy().add(0, 1, 0));
		cam.up.set(0, 1, 0); // should this be doable without this?
		cam.update(true);
	}

	@Override
	public void onDeath() {
		// TODO
		System.out.println("Dead!");
	}

	public void processInput() {
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			controller.setWorldTransform(controller.getWorldTransform().rotate(0, 1, 0, 5f));
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			controller.setWorldTransform(controller.getWorldTransform().rotate(0, 1, 0, -5f));
		}
		characterDirection.set(0, 0, 1).rot(getTransform()).nor();
		walkDirection.set(0, 0, 0);

		if (Gdx.input.isKeyPressed(Keys.UP)) {
			walkDirection.add(characterDirection);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			walkDirection.add(-characterDirection.x, -characterDirection.y, -characterDirection.z);
		}
		walkDirection.scl(8f * Gdx.graphics.getDeltaTime());
		controller.setWalkDirection(walkDirection);
	}

	@Override
	public InputProcessor getInputProcessor() {
		return new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.SPACE && Gdx.input.isKeyPressed(Keys.UP)) {
					controller.bigJump();
					return true;
				} else if (keycode == Keys.SPACE) {
					controller.jump();
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected void updateTransform() {
		super.updateTransform();
		this.controller.getGhostObject().setWorldTransform(getTransform());
	}

	@Override
	public void setMass(float mass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispose() {
		this.game.getBtWorld().removeAction(controller);
		super.dispose();
	}

	static {
		WandererConstants.ASSET_MANAGER.load("orig/lib/sound/step_bigland_john.wav", Sound.class);
	}
}
