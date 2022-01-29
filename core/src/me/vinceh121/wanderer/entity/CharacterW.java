package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import me.vinceh121.wanderer.Wanderer;

/**
 * An entity of a character, a person.
 *
 * Named like that to differentiate with java.lang.Character
 */
public class CharacterW extends AbstractLivingEntity implements IControllableEntity {

	public CharacterW(Wanderer game) {
		this(game, 0.3f, 1.5f);
	}

	public CharacterW(Wanderer game, float capsuleRadius, float capsuleHeight) {
		super(game);
		this.setCollideObject(
				new btRigidBody(1, createMotionState(), new btCapsuleShape(capsuleRadius, capsuleHeight)));
		this.setCollideObjectOffset(new Vector3(0, 0.8f, 0));
	}

	@Override
	public void onDeath() {
		// TODO
		System.out.println("Dead!");
	}

	@Override
	public InputProcessor getInputProcessor() {
		// TODO Auto-generated method stub
		return null;
	}
}
