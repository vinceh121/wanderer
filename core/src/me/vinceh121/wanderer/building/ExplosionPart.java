package me.vinceh121.wanderer.building;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.util.MathUtilsW;

public class ExplosionPart extends AbstractEntity {

	public ExplosionPart(Wanderer game, DisplayModel model) {
		super(game);

		this.setMass(0.01f);
		this.setExactCollideModel(false);
		this.setCollisionGroup(CollisionFilterGroups.DebrisFilter);
		this.setCollisionMask(CollisionFilterGroups.DefaultFilter | CollisionFilterGroups.KinematicFilter
				| CollisionFilterGroups.StaticFilter);
		this.setCollideModel(model.getDisplayModel());

		DisplayModel mdl = new DisplayModel(model);
		this.setTransform(mdl.getRelativeTransform());
		mdl.getRelativeTransform().idt();
		this.addModel(mdl);
	}

	public void thrust(float strength) {
		this.getCollideObject().setLinearVelocity(MathUtilsW.randomDirectionAround(Vector3.Y, 0.1f).scl(strength));
		this.getCollideObject()
			.setAngularVelocity(new Vector3(MathUtils.random(-1, 1), MathUtils.random(-1, 1), MathUtils.random(-1, 1)));
	}
}
