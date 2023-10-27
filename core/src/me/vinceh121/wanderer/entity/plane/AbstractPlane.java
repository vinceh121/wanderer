package me.vinceh121.wanderer.entity.plane;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.building.ExplosionPart;
import me.vinceh121.wanderer.entity.AbstractClanLivingEntity;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.entity.IControllableEntity;
import me.vinceh121.wanderer.input.Input;
import me.vinceh121.wanderer.input.InputListener;
import me.vinceh121.wanderer.input.InputListenerAdapter;

public abstract class AbstractPlane extends AbstractClanLivingEntity implements IControllableEntity {
	private final Array<DisplayModel> explosionParts = new Array<>();
	private final PlaneSpeedProfile normal, turbo;
	private boolean controlled;
	private float speedUpTime;

	public AbstractPlane(Wanderer game, AbstractPlaneMeta meta) {
		super(game);

		this.setCollideModel(meta.getCollisionModel());

		for (final DisplayModel m : meta.getDisplayModels()) {
			this.getModels().add(new DisplayModel(m));
		}

		for (final DisplayModel m : meta.getExplosionParts()) {
			this.explosionParts.add(new DisplayModel(m));
		}

		this.normal = new PlaneSpeedProfile(meta.getNormal());
		this.turbo = new PlaneSpeedProfile(meta.getTurbo());
	}

	@Override
	public void tick(final float delta) {
		super.tick(delta);
		
		if (this.controlled) {
			if (this.game.getInputManager().isPressed(Input.FLY_BOOST)) {
				this.speedUpTime += delta; // FIXME upper clamp
			} else {
				this.speedUpTime = Math.max(0, this.speedUpTime - delta);
			}
		}

		final float speedUpProgress = this.speedUpTime / this.normal.getAcceleration();
		final float speed = MathUtils.lerp(this.normal.getMinSpeed(), this.normal.getMaxSpeed(), speedUpProgress);

		this.translate(0, 0, -speed * delta);

		if (this.controlled) {
			this.moveCamera();

			if (this.game.getInputManager().isPressed(Input.FIRE)) {
				this.fire();
			}
		}
	}

	protected void moveCamera() {
		final Vector3 arm = new Vector3(0, 6, 17);
		arm.mul(this.getRotation());
		arm.add(this.getTranslation());

		final Vector3 watch = new Vector3(0, 0, -50);
		watch.mul(this.getRotation());
		watch.add(this.getTranslation());

		this.game.getCamera().position.set(arm);
		this.game.getCamera().lookAt(watch);
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
	}

	@Override
	public void onTakeControl() {
		this.controlled = true;
	}

	@Override
	public void onRemoveControl() {
		this.controlled = false;
	}

	@Override
	public InputListener getInputProcessor() {
		return new InputListenerAdapter(50);
	}
}
