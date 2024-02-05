package me.vinceh121.wanderer.entity.plane;

import java.util.Optional;

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

import me.vinceh121.wanderer.Preferences;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.ai.AIController;
import me.vinceh121.wanderer.ai.Task;
import me.vinceh121.wanderer.ai.TaskAIController;
import me.vinceh121.wanderer.building.ExplosionPart;
import me.vinceh121.wanderer.clan.Amicability;
import me.vinceh121.wanderer.combat.DamageType;
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
	private AIController<? extends AbstractPlane> aiController;
	private String explosionSound;
	private InputListener inputListener;
	private ColorAttribute colorAttr;
	private BlendingAttribute blendingAttr;
	private Garage garage;
	private boolean controlled, isTurbo;
	private float speedUpTime, maxTurboTime, turboTime, yaw, pitch, roll, currentYawTime, currentPitchTime,
			currentRollTime, targetSearchDistance;
	private long turboPressTime;

	public AbstractPlane(final Wanderer game, final AbstractPlanePrototype prototype) {
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

		this.targetSearchDistance = prototype.getTargetSearchDistance();

		this.engineEmitter =
				WandererConstants.getAssetOrHotload(prototype.getEngineSound(), Sound3D.class).playSource3D();
		this.engineEmitter.setLooping(true);
		this.addSoundEmitter(this.engineEmitter);

		this.turboEmitter =
				WandererConstants.getAssetOrHotload(prototype.getTurboSound(), Sound3D.class).playSource3D();
		this.turboEmitter.stop();
		this.addSoundEmitter(this.turboEmitter);

		this.setupAi();
	}

	protected void setupAi() {
		final TaskAIController<AbstractPlane> cont = new TaskAIController<>(game, this);
		cont.setCurrentTask(new TaskTargetSearch<AbstractPlane>());
		this.aiController = cont;
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

		final PlaneSpeedProfile profile = this.getCurrentProfile();

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
			final int mouseSensX = Preferences.getPreferences().getIntOrElse("input.flight.xSens", 20);
			final int mouseSensY = Preferences.getPreferences().getIntOrElse("input.flight.ySens", 20);

			if (this.game.getInputManager().isPressed(Input.FLY_UP)) {
				this.currentPitchTime = Math.min(this.currentPitchTime + delta, profile.getPitchSpeed());
			} else if (this.game.getInputManager().isPressed(Input.FLY_DOWN)) {
				this.currentPitchTime = Math.max(this.currentPitchTime - delta, -profile.getPitchSpeed());
			} else if (this.game.getInputManager().getLastMouseX() != 0) {
				this.currentPitchTime = MathUtils
					.map(0, mouseSensY, 0, profile.getPitchSpeed(), this.game.getInputManager().getLastMouseY());
			} else {
				this.currentPitchTime = 0;
			}

			if (this.game.getInputManager().isPressed(Input.FLY_LEFT)) {
				this.currentRollTime = Math.min(this.currentRollTime + delta, profile.getRollTime());
				this.currentYawTime = Math.min(this.currentYawTime + delta, profile.getYawSpeed());
			} else if (this.game.getInputManager().isPressed(Input.FLY_RIGHT)) {
				this.currentRollTime = Math.max(this.currentRollTime - delta, -profile.getRollTime());
				this.currentYawTime = Math.max(this.currentYawTime - delta, -profile.getYawSpeed());
			} else if (this.game.getInputManager().getLastMouseY() != 0) {
				this.currentYawTime = MathUtils
					.map(0, mouseSensX, 0, profile.getYawSpeed(), this.game.getInputManager().getLastMouseX());
				this.currentRollTime = MathUtils
					.map(0, mouseSensX, 0, profile.getRollTime(), this.game.getInputManager().getLastMouseX());
			} else {
				final float signRoll = Math.signum(this.roll);

				this.currentRollTime = this.currentYawTime = 0;

				// FIXME need to use delta in here somewhere
				this.roll = signRoll > 0 ? Math.max(this.roll - profile.getRollTime(), 0)
						: Math.min(this.roll + profile.getRollTime(), 0);
			}

		} else if (this.aiController != null) {
			this.aiController.tick(delta);
		}

		this.yaw = (this.yaw + this.currentYawTime) % 360;
		this.pitch = MathUtils.clamp(this.currentPitchTime + this.pitch, -profile.getMaxPitch(), profile.getMaxPitch());
		this.roll = MathUtils.clamp(this.currentRollTime + this.roll, -profile.getMaxRoll(), profile.getMaxRoll());

		MathUtilsW.setRotation(this.getTransform(), new Quaternion().setEulerAngles(this.yaw, this.pitch, this.roll));

		this.advance(speed * delta);

		if (this.controlled) {
			this.moveCamera(delta);

			if (this.game.getInputManager().isPressed(Input.FIRE)) {
				this.fire();
			}
		}
	}

	public PlaneSpeedProfile getCurrentProfile() {
		return this.isTurbo ? this.turbo : this.normal;
	}

	public void turnTowards(final Vector3 pos, final float delta) {
		final Vector3 myDir = new Vector3(0, 1, 0).mul(this.getRotation());
		final Vector3 dif = this.getTranslation().sub(pos).nor();

		float toYaw = Math.abs(MathUtils.atan2(myDir.z, myDir.x) * MathUtils.radiansToDegrees
				- MathUtils.atan2(dif.z, dif.x) * MathUtils.radiansToDegrees) - 90;
//		final float toPitch = MathUtils.asin(dir.z);

		final PlaneSpeedProfile profile = this.getCurrentProfile();

		this.currentRollTime = Math.max(this.currentRollTime - delta, Math.signum(toYaw) * profile.getRollTime());
		this.currentYawTime = Math.max(this.currentYawTime - delta, Math.signum(toYaw) * profile.getYawSpeed());
//		this.currentPitchTime = Math.max(this.currentPitchTime - delta, -profile.getYawSpeed());
	}

	protected void advance(final float dist) {
		if (this.getCollideObject() != null) {
			final Matrix4 start = this.getTransform();
			final Vector3 startVec = start.getTranslation(new Vector3());

			final Matrix4 end = this.getTransform().cpy();
			end.translate(0, 0, -dist);
			final Vector3 endVec = end.getTranslation(new Vector3());

			final ClosestNotMeConvexResultCallback cb =
					new ClosestNotMeConvexResultCallback(this.getCollideObject(), startVec, endVec);

			this.game.getBtWorld()
				.convexSweepTest((btConvexShape) this.getCollideObject().getCollisionShape(), start, end, cb);

			startVec.lerp(endVec, cb.getClosestHitFraction());
			this.setTranslation(startVec);

			if (cb.hasHit()) {
				this.damage(Float.POSITIVE_INFINITY, DamageType.COLLISION);
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
			.playSource3D(1, this.getTranslation())
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

	protected InputListener createInputProcessor() {
		return new InputListenerAdapter(50) {
			@Override
			public boolean inputDown(final Input in) {
				if (in == Input.FLY_BOOST) {
					if (System.currentTimeMillis()
							- AbstractPlane.this.turboPressTime < AbstractPlane.DOUBLE_TAP_SENSITIVITY) {
						AbstractPlane.this.turbo();
					}

					AbstractPlane.this.turboPressTime = System.currentTimeMillis();
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

	public float getTargetSearchDistance() {
		return targetSearchDistance;
	}

	public void setTargetSearchDistance(float targetSearchDistance) {
		this.targetSearchDistance = targetSearchDistance;
	}

	public Garage getGarage() {
		return garage;
	}

	public void setGarage(Garage garage) {
		this.garage = garage;
	}

	public static class TaskTargetSearch<T extends AbstractPlane> extends Task<T> {
		@Override
		public Task<T> process(final float delta, final Wanderer game, final T controlled) {
			if (controlled.getClan() == null) {
				return null;
			}

			final Vector3 pos = controlled.getTranslation();

			final Optional<AbstractClanLivingEntity> newTarget = game.streamEntities()
				.filter(e -> e instanceof AbstractClanLivingEntity) // XXX should I only compare this to interfaces?
				.map(e -> (AbstractClanLivingEntity) e)
				.filter(e -> controlled.getClan() != e.getClan()
						&& controlled.getClan().getRelationship(e.getClan()) == Amicability.HOSTILE)
				.filter(e -> pos.dst(e.getTranslation()) < controlled.getTargetSearchDistance())
				.sorted((o1, o2) -> Float.compare(pos.dst(o1.getTranslation()), pos.dst(o2.getTranslation())))
				.findFirst();

			if (newTarget.isPresent()) {
				return new TaskGotoTarget<T>(newTarget.get());
			}

			return null;
		}
	}

	public static class TaskGotoTarget<T extends AbstractPlane> extends Task<T> {
		private final AbstractClanLivingEntity target;

		public TaskGotoTarget(AbstractClanLivingEntity target) {
			this.target = target;
		}

		@Override
		public Task<T> process(float delta, Wanderer game, T controlled) {
			if (target.isDisposed() || target.isDead()) {
				return new TaskTargetSearch<T>();
			}

			controlled.turnTowards(this.target.getTranslation(), delta);

			return null;
		}
	}

}
