package me.vinceh121.wanderer.guntower;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.ai.AIController;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.util.MathUtilsW;

public class GuntowerAiController extends AIController<AbstractGuntower> {
	private final Set<String> targets = new HashSet<>();
	private float range = 500, turnSpeed = 3;

	public GuntowerAiController(Wanderer game, AbstractGuntower target) {
		super(game, target);
	}

	@Override
	public void tick(float delta) {
		AbstractEntity closest = null;
		float minDist = Float.MAX_VALUE;

		for (final AbstractEntity e : this.game.getEntities()) {
			final float dist = this.target.getTranslation().dst(e.getTranslation());

			if (dist < minDist && e != this.target && targets.contains(e.getClass().getCanonicalName())) {
				closest = e;
				minDist = dist;
			}
		}

		if (minDist > range || closest == null) {
			return;
		}

		final Vector3 closestDir = closest.getMidPoint()
			.cpy()
			.sub(this.target.getTranslation().add(0, this.target.getAverageTurretPosition().y, 0))
			.nor();

		final Vector3 newDir = this.target.getLookDirection().slerp(closestDir, this.turnSpeed * delta);
		newDir.rotateRad(Vector3.X, MathUtils.HALF_PI);

		final float polar = MathUtilsW.getSphericalPolar(newDir.z);
		final float azimuth = MathUtilsW.getSphericalAzimuth(newDir.x, newDir.y);

		this.target.setPolarAngle(1 - (polar / MathUtils.PI));
		this.target.setAzimuth(1 - (azimuth / MathUtils.PI2));

		final float angle = this.target.getLookDirection().dot(closestDir);

		if (MathUtils.isEqual(angle, 1f, 0.1f)) {
			this.target.fire();
		}
	}
}
