package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;

/**
 * An entity of a character, a person.
 *
 * Named like that to differentiate with java.lang.Character
 */
public class CharacterW extends AbstractLivingControllableEntity implements IClanMember {
	private final CharacterWController controller;
	private final Vector3 characterDirection = new Vector3();
	private final Vector3 walkDirection = new Vector3();
	private Clan clan;

	public CharacterW(final Wanderer game) {
		this(game, 0.3f, 1.5f);
	}

	public CharacterW(final Wanderer game, final float capsuleRadius, final float capsuleHeight) {
		super(game);
		this.setCollideObjectOffset(new Vector3(0, 0.8f, 0));
		this.controller = new CharacterWController(this);
		this.controller.setFallListener(this::onFall);
		game.getBtWorld().addAction(this.controller);
	}

	private void onFall(final boolean bigJump) {
		WandererConstants.ASSET_MANAGER.get("orig/lib/sound/step_bigland_john.wav", Sound.class).play();
	}

	@Override
	public void updatePhysics(final btDiscreteDynamicsWorld world) {
		super.updatePhysics(world);

		final Matrix4 colTransform = this.controller.getGhostObject().getWorldTransform();
		final Vector3 colTranslation = new Vector3();
		colTransform.getTranslation(colTranslation);
		colTranslation.sub(this.getCollideObjectOffset());
		colTransform.setTranslation(colTranslation);

		// do not call setTransform as not to cause update to ghostobject
		this.getTransform().set(colTransform);
	}

	@Override
	public void render(final ModelBatch batch, final Environment env) {
		super.render(batch, env);
		if (this.isControlled()) {
			this.processInput();
			this.moveCamera();
		}
	}

	private void moveCamera() {
		final PerspectiveCamera cam = this.game.getCamera();
		final Vector3 characterTransform = new Vector3();
		this.getTransform().getTranslation(characterTransform);

		final Quaternion characterRotation = new Quaternion();
		this.getTransform().getRotation(characterRotation);

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
			this.controller.setWorldTransform(this.controller.getWorldTransform().rotate(0, 1, 0, 5f));
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			this.controller.setWorldTransform(this.controller.getWorldTransform().rotate(0, 1, 0, -5f));
		}
		this.characterDirection.set(0, 0, 1).rot(this.getTransform()).nor();
		this.walkDirection.set(0, 0, 0);

		if (Gdx.input.isKeyPressed(Keys.UP)) {
			this.walkDirection.add(this.characterDirection);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			this.walkDirection.add(-this.characterDirection.x, -this.characterDirection.y, -this.characterDirection.z);
		}
		this.walkDirection.scl(8f * Gdx.graphics.getDeltaTime());
		this.controller.setWalkDirection(this.walkDirection);
	}

	@Override
	public InputProcessor getInputProcessor() {
		return new InputAdapter() {
			@Override
			public boolean keyDown(final int keycode) {
				if (keycode == Keys.SPACE && Gdx.input.isKeyPressed(Keys.UP)) {
					CharacterW.this.controller.bigJump();
					return true;
				} else if (keycode == Keys.SPACE) {
					CharacterW.this.controller.jump();
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected void updateTransform() {
		super.updateTransform();
		this.controller.getGhostObject().setWorldTransform(this.getTransform());
	}

	@Override
	public void setMass(final float mass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispose() {
		this.game.getBtWorld().removeAction(this.controller);
		super.dispose();
	}

	static {
		WandererConstants.ASSET_MANAGER.load("orig/lib/sound/step_bigland_john.wav", Sound.class);
	}

	@Override
	public Clan getClan() {
		return this.clan;
	}

	@Override
	public void onJoinClan(final Clan clan) {
		this.clan = clan;
		// TODO change light decals colors
	}
}
