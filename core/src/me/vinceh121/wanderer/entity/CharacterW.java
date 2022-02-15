package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import me.vinceh121.wanderer.Wanderer;

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
		this.setCollideObject(
				new btRigidBody(1, createMotionState(), new btCapsuleShape(capsuleRadius, capsuleHeight)));
		this.getCollideObject().setAngularFactor(1);
		this.setCollideObjectOffset(new Vector3(0, 0.8f, 0));
		this.getCollideObject().setFriction(100);
		this.controller = new CharacterWController(this);
		game.getBtWorld().addAction(this.controller);
	}

	@Override
	public void render(ModelBatch batch, Environment env) {
		super.render(batch, env);
		this.processInput();
	}

	@Override
	public void onDeath() {
		// TODO
		System.out.println("Dead!");
	}

	public void processInput() {
		if (!this.isControlled())
			return;

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			getTransform().rotate(0, 1, 0, 5f);
			this.getCollideObject().setWorldTransform(getTransform());
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			getTransform().rotate(0, 1, 0, -5f);
			this.getCollideObject().setWorldTransform(getTransform());
			this.getCollideObject().activate();
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
//		controller.setWalkDirection(walkDirection);
	}

	@Override
	public InputProcessor getInputProcessor() {
		return new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.SPACE) {
					controller.jump();
					return true;
				}
				return false;
			}
		};
	}

	@Override
	public void setMass(float mass) {
		throw new UnsupportedOperationException();
	}
}
