package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.CustomActionInterface;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.WandererConstants;

public class CharacterWController extends CustomActionInterface {
	private final ContactListener contactListener = new ContactListener() {
		public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
			if (colObj0.getCPointer() == character.getCollideObject().getCPointer()
					|| colObj1.getCPointer() == character.getCollideObject().getCPointer()) {
				stopJump();
			}
		};
	};
	private CharacterW character;
	private boolean jumping;
	private float jumpProgress;
	private Bezier<Vector3> jumpCurve;

	public CharacterWController(CharacterW character) {
		this.character = character;
		this.contactListener.enable();
	}

	public void jump() {
		if (this.jumping)
			return;
		final Array<Vector3> points = new Array<>(3);
		points.add(this.character.getCollideObject().getWorldTransform().getTranslation(new Vector3()));
		points.add(points.peek().cpy().add(0, 1, 0));
		points.add(points.first().cpy().add(0, 0, 1));
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
			Matrix4 trans = this.character.getTransform();
			trans.setTranslation(this.jumpCurve.valueAt(new Vector3(), jumpProgress));
			this.character.setTransform(trans);
			jumpProgress += deltaTimeStep;
			this.character.getCollideObject().activate();
		}
	}

	@Override
	public void debugDraw() {
	}

	@Override
	public void dispose() {
		this.contactListener.dispose();
		super.dispose();
	}

	static {
		WandererConstants.ASSET_MANAGER.load("orig/lib/sound/step_bigland_john.wav", Sound.class);
	}
}