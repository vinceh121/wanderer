package me.vinceh121.wanderer.entity.plane;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeConvexResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.building.ExplosionPart;
import me.vinceh121.wanderer.entity.AbstractClanLivingEntity;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.entity.IControllableEntity;
import me.vinceh121.wanderer.input.Input;
import me.vinceh121.wanderer.input.InputListener;
import me.vinceh121.wanderer.input.InputListenerAdapter;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.platform.audio.SoundEmitter3D;
import me.vinceh121.wanderer.util.MathUtilsW;

public abstract class AbstractPlane extends AbstractClanLivingEntity implements IControllableEntity {
	private static final long DOUBLE_TAP_SENSITIVITY = 500;
	private final Array<DisplayModel> explosionParts = new Array<>();
	private final PlaneSpeedProfile normal, turbo;
	protected final SoundEmitter3D engineEmitter, turboEmitter;
	private String explosionSound;;
	private InputListener inputListener;
	private ColorAttribute colorAttr;
	private BlendingAttribute blendingAttr;
	private boolean controlled, isTurbo;
	private float speedUpTime, maxTurboTime, turboTime, yaw, pitch, roll, currentYawTime, currentPitchTime,
			currentRollTime;
	private long turboPressTime;

	public AbstractPlane(Wanderer game, AbstractPlanePrototype prototype) {
		super(game);

		this.setExactCollideModel(false);

		this.setCollideModel(prototype.getCollisionModel());

		for (final DisplayModel m : prototype.getDisplayModels()) {
			this.getModels().add(new DisplayModel(m));
		}

		for (final DisplayModel m : prototype.getExplosionParts()) {
			this.explosionParts.add(new DisplayModel(m));
		}

		this.normal = new PlaneSpeedProfile(prototype.getNormal());
		this.turbo = new PlaneSpeedProfile(prototype.getTurbo());

		this.maxTurboTime = prototype.getMaxTurboTime();

		this.explosionSound = prototype.getExplosionSound();

		this.engineEmitter =
				WandererConstants.getAssetOrHotload(prototype.getEngineSound(), Sound3D.class).playSource3D();
		this.engineEmitter.setLooping(true);
		this.addSoundEmitter(this.engineEmitter);

		this.turboEmitter =
				WandererConstants.getAssetOrHotload(prototype.getTurboSound(), Sound3D.class).playSource3D();
		this.turboEmitter.stop();
		this.addSoundEmitter(this.turboEmitter);

	}

	@Override
	public void tick(final float delta) {
		super.tick(delta);

		if (this.isTurbo) {
			this.turboTime = Math.max(0, this.turboTime - delta);

			if (this.turboTime == 0) {
				this.unturbo();
			}
		}

		final PlaneSpeedProfile profile = this.isTurbo ? this.turbo : this.normal;

		if (this.controlled) {
			if (this.game.getInputManager().isPressed(Input.FLY_BOOST)) {
				this.speedUpTime = Math.min(delta + this.speedUpTime, profile.getAcceleration());
			} else {
				this.speedUpTime = Math.max(0, this.speedUpTime - delta);
			}
		}

		final float speedUpProgress = this.speedUpTime / profile.getAcceleration();
		final float speed = MathUtils.lerp(profile.getMinSpeed(), profile.getMaxSpeed(), speedUpProgress);

		if (this.controlled) {
			if (this.game.getInputManager().isPressed(Input.FLY_UP)) {
				this.currentPitchTime = Math.min(this.currentPitchTime + delta, profile.getPitchSpeed());
			} else if (this.game.getInputManager().isPressed(Input.FLY_DOWN)) {
				this.currentPitchTime = Math.max(this.currentPitchTime - delta, -profile.getPitchSpeed());
			} else {
				this.currentPitchTime = 0;
			}

			if (this.game.getInputManager().isPressed(Input.FLY_LEFT)) {
				this.currentRollTime = Math.min(this.currentRollTime + delta, profile.getRollTime());
				this.currentYawTime = Math.min(this.currentYawTime + delta, profile.getYawSpeed());
			} else if (this.game.getInputManager().isPressed(Input.FLY_RIGHT)) {
				this.currentRollTime = Math.max(this.currentRollTime - delta, -profile.getRollTime());
				this.currentYawTime = Math.max(this.currentYawTime - delta, -profile.getYawSpeed());
			} else {
				this.currentRollTime = 0;
				this.currentYawTime = 0;
				this.roll = 0;
			}

			this.yaw += this.currentYawTime;
			this.pitch =
					MathUtils.clamp(this.currentPitchTime + this.pitch, -profile.getMaxPitch(), profile.getMaxPitch());
			this.roll = MathUtils.clamp(this.currentRollTime + this.roll, -profile.getMaxRoll(), profile.getMaxRoll());

			MathUtilsW.setRotation(getTransform(), new Quaternion().setEulerAngles(this.yaw, this.pitch, this.roll));
		}

		this.advance(speed * delta);

		if (this.controlled) {
			this.moveCamera(delta);

			if (this.game.getInputManager().isPressed(Input.FIRE)) {
				this.fire();
			}
		}
	}

	protected void advance(float dist) {
		if (this.getCollideObject() != null) {
			final Matrix4 start = this.getTransform();
			final Vector3 startVec = start.getTranslation(new Vector3());

			final Matrix4 end = this.getTransform().cpy();
			end.translate(0, 0, -dist);
			final Vector3 endVec = end.getTranslation(new Vector3());

			final ClosestNotMeConvexResultCallback cb =
					new ClosestNotMeConvexResultCallback(getCollideObject(), startVec, endVec);

			this.game.getBtWorld()
				.convexSweepTest((btConvexShape) this.getCollideObject().getCollisionShape(), start, end, cb);

			startVec.lerp(endVec, cb.getClosestHitFraction());
			this.setTranslation(startVec);

			if (cb.hasHit()) {
				this.onDeath();
			}

			cb.dispose();
		} else {
			this.translate(0, 0, -dist);
		}
	}

	public void turbo() {
		if (this.isTurbo) {
			return;
		}

		this.isTurbo = true;
		this.turboEmitter.play();
		this.engineEmitter.pause();
		this.turboTime = this.maxTurboTime;
		this.colorAttr = ColorAttribute.createEmissive(this.getClan() == null ? Color.GRAY : this.getClan().getColor());
		this.blendingAttr = new BlendingAttribute(0.5f);

		for (final DisplayModel mdl : this.getFlatModels()) {
			mdl.addTextureAttribute(this.colorAttr);
			mdl.addTextureAttribute(this.blendingAttr);
		}
	}

	protected void unturbo() {
		this.isTurbo = false;
		this.turboEmitter.stop();
		this.engineEmitter.play();

		for (final DisplayModel mdl : this.getFlatModels()) {
			mdl.removeTextureAttribute(this.colorAttr);
			mdl.removeTextureAttribute(this.blendingAttr);
		}
	}

	protected void moveCamera(final float delta) {
		final Vector3 arm = new Vector3(0, 6, 17);
		arm.mul(this.getRotation());
		arm.add(this.getTranslation());

		final Vector3 watch = new Vector3(0, 0, -50);
		watch.mul(this.getRotation());
		watch.add(this.getTranslation());

		this.game.getCamera().position.set(arm);
		this.game.getCamera().lookAt(watch);
		this.game.getCamera().up.set(0, 1, 0);
	}

	public abstract void fire();

	@Override
	public void onDeath() {
		this.game.removeEntity(this);
		this.dispose();

		for (final DisplayModel m : this.explosionParts) {
			final ExplosionPart part = new ExplosionPart(this.game, m);
			part.translate(this.getTranslation());
			part.addEventListener("collideModelLoaded", e -> part.thrust(10));
			this.game.addEntity(part);
		}

		WandererConstants.getAssetOrHotload(this.explosionSound, Sound3D.class)
			.playSource3D(1, getTranslation())
			.setDisposeOnStop(true);

		this.dead = true;
	}

	@Override
	public void onTakeControl() {
		this.controlled = true;
	}

	@Override
	public void onRemoveControl() {
		this.controlled = false;
	}

	public InputListener createInputProcessor() {
		return new InputListenerAdapter(50) {
			@Override
			public boolean inputDown(Input in) {
				if (in == Input.FLY_BOOST) {
					if (System.currentTimeMillis() - turboPressTime < DOUBLE_TAP_SENSITIVITY) {
						turbo();
					}

					turboPressTime = System.currentTimeMillis();
					return true;
				}

				return false;
			}
		};
	}

	@Override
	public InputListener getInputProcessor() {
		if (this.inputListener == null) {
			this.inputListener = this.createInputProcessor();
		}

		return this.inputListener;
	}
}
