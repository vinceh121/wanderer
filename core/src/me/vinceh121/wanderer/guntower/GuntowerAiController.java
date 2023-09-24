package me.vinceh121.wanderer.guntower;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.ai.AIController;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.util.MathUtilsW;

public class GuntowerAiController extends AIController<AbstractGuntower> {
	private final List<String> priorities = new ArrayList<>();
	private float range = 500, turnSpeed = 3;

	public GuntowerAiController(Wanderer game, AbstractGuntower target) {
		super(game, target);
	}

	@Override
	public void tick(float delta) {
		final Queue<AbstractEntity> around = new PriorityQueue<>((e1, e2) -> {
			final int typeCmp = Integer.compare(this.priorities.indexOf(e1.getClass().getCanonicalName()),
					this.priorities.indexOf(e2.getClass().getCanonicalName()));

			if (typeCmp != 0) {
				return typeCmp;
			} else {
				return Float.compare(e1.getTranslation().dst2(this.target.getTranslation()),
						e2.getTranslation().dst2(this.target.getTranslation()));
			}
		});

		float minDist = Float.MAX_VALUE;

		for (final AbstractEntity e : this.game.getEntities()) {
			final float dist = this.target.getTranslation().dst(e.getTranslation());

			if (dist < minDist) {
				minDist = dist;
			}

			if (e != this.target && priorities.contains(e.getClass().getCanonicalName())) {
				around.add(e);
			}
		}

		if (minDist > range || around.size() == 0) {
			return;
		}

		final AbstractEntity closest = around.element();

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
