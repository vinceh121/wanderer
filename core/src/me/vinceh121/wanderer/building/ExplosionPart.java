package me.vinceh121.wanderer.building;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.entity.ParticleEmitter;
import me.vinceh121.wanderer.util.MathUtilsW;

public class ExplosionPart extends AbstractEntity {
	private float lifetime, maxLifetime = 3;

	public ExplosionPart(final Wanderer game, final DisplayModel model) {
		super(game);

		this.setMass(0.01f);
		this.setExactCollideModel(false);
		this.setCollisionGroup(CollisionFilterGroups.DebrisFilter);
		this.setCollisionMask(CollisionFilterGroups.DefaultFilter | CollisionFilterGroups.KinematicFilter
				| CollisionFilterGroups.StaticFilter);
		this.setCollideModel(model.getDisplayModel());

		final DisplayModel mdl = new DisplayModel(model);
		this.setTransform(mdl.getRelativeTransform());
		mdl.getRelativeTransform().idt();
		this.addModel(mdl);

		this.addParticle(
				new ParticleEmitter(this.game.getGraphicsManager().getParticleSystem(), "particles/debrisfire1.p"));
	}

	public void thrust(final float strength) {
		this.getCollideObject().setLinearVelocity(MathUtilsW.randomDirectionAround(Vector3.Y, 0.1f).scl(strength));
		this.getCollideObject()
			.setAngularVelocity(new Vector3(MathUtils.random(-1, 1), MathUtils.random(-1, 1), MathUtils.random(-1, 1)));
	}

	@Override
	public void tick(float delta) {
		super.tick(delta);

		this.lifetime += delta;

		if (this.lifetime > this.maxLifetime) {
			this.game.removeEntity(this);
			this.dispose();
		}
	}

	public float getLifetime() {
		return lifetime;
	}

	public void setLifetime(float lifetime) {
		this.lifetime = lifetime;
	}

	public float getMaxLifetime() {
		return maxLifetime;
	}

	public void setMaxLifetime(float maxLifetime) {
		this.maxLifetime = maxLifetime;
	}
}
