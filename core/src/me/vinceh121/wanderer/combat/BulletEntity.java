package me.vinceh121.wanderer.combat;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.entity.ParticleEmitter;

public class BulletEntity extends AbstractEntity {
	private final Vector3 direction = new Vector3();
	private float speed = 200, distance, progress;
	private boolean hasHit, playingParticle;
	private ParticleEmitter hit;

	public BulletEntity(Wanderer game) {
		super(game);

		this.setCastShadow(false);

		DisplayModel model = new DisplayModel("orig/lib/standards/sprite.obj", "orig/lib/explo18/flametipnone.ktx");
		model.addTextureAttribute(IntAttribute.createCullFace(0));
		model.addTextureAttribute(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_COLOR, 1f));
		this.addModel(model);

		hit = new ParticleEmitter(this.game.getGraphicsManager().getParticleSystem(), "particles/bullethit.p");
	}

	@Override
	public void tick(float delta) {
		super.tick(delta);

		if (this.playingParticle) {
			if (this.hit.getDelegate() != null && this.hit.getDelegate().isComplete()) {
				this.game.removeEntity(this);
				this.dispose();
			}

			return;
		}

		final float step = delta * this.speed;
		this.translate(this.direction.cpy().scl(step).mul(getRotation().conjugate()));

		this.progress += step;

		if (this.progress >= this.distance) {
			this.addParticle(hit);
			this.updateTransform();
			this.playingParticle = true;
		}
	}

	public Vector3 getDirection() {
		return this.direction;
	}

	public void setDirection(Vector3 direction) {
		this.direction.set(direction);
	}

	public float getSpeed() {
		return this.speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public boolean isHasHit() {
		return hasHit;
	}

	public void setHasHit(boolean hasHit) {
		this.hasHit = hasHit;
	}
}
