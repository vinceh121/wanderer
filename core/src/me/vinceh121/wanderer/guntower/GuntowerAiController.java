package me.vinceh121.wanderer.guntower;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.ai.AIController;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.ILivingEntity;
import me.vinceh121.wanderer.util.MathUtilsW;

public class GuntowerAiController extends AIController<AbstractGuntower> {
	private final Array<String> targets = new Array<>(true, 5);
	private float range = 500, turnSpeed = 5;

	public GuntowerAiController(Wanderer game, AbstractGuntower target) {
		super(game, target);
	}

	@Override
	public void tick(float delta) {
		AbstractEntity closest = null;
		float minDist = Float.MAX_VALUE;

		for (final AbstractEntity e : this.game.getEntities()) {
			final float dist = this.target.getTranslation().dst(e.getTranslation());

			if (dist < minDist && e != this.target && e instanceof ILivingEntity) {
				closest = e;
				minDist = dist;
			}
		}

		if (minDist > range || closest == null) {
			return;
		}

		final Vector3 closestDir = closest.getTranslation().cpy().sub(this.target.getTranslation());
		final float angle = this.target.getLookDirection().dot(closestDir);

		if (MathUtils.isZero(angle, 1)) {
			this.target.fire();
		}

		final Vector3 newDir = this.target.getLookDirection().slerp(closestDir, this.turnSpeed * delta);
		final float r = newDir.len();
		
		final float polar = MathUtilsW.getSphericalPolar(newDir.z, r);
		final float azimuth = MathUtilsW.getSphericalAzimuth(newDir.x, newDir.y);
		
		System.out.println(polar);
		System.out.println(azimuth);
		System.out.println();

		this.target.setPolarAngle(polar);
		this.target.setAzimuth(azimuth);
	}
}
