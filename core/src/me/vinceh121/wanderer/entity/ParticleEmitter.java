package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.annotation.JsonIgnore;

import me.vinceh121.wanderer.WandererConstants;

public class ParticleEmitter {
	@JsonIgnore
	private ParticleSystem system;
	private final Matrix4 relativeTransform = new Matrix4();
	@JsonIgnore
	private final Matrix4 absoluteTransform = new Matrix4();
	@JsonIgnore
	private ParticleEffect delegate;
	private String particle;

	public ParticleEmitter() {
	}

	public ParticleEmitter(final ParticleSystem system, final String particle) {
		this.system = system;
		this.particle = particle;
	}

	public ParticleEffect getDelegate() {
		return delegate;
	}

	public void setDelegate(ParticleEffect delegate) {
		this.delegate = delegate;
	}

	public void updateLoading() {
		if (this.particle != null && this.delegate == null) {
			if (WandererConstants.ASSET_MANAGER.isLoaded(this.particle, ParticleEffect.class)) {
				this.delegate = WandererConstants.ASSET_MANAGER.get(this.particle, ParticleEffect.class).copy();
				this.delegate.init();
				this.delegate.start();
				this.system.add(delegate);
			} else {
				ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(this.system.getBatches());
				WandererConstants.ASSET_MANAGER.load(this.particle, ParticleEffect.class, loadParam);
			}
		}
	}

	public void start() {
		delegate.start();
	}

	public void end() {
		delegate.end();
	}

	public void reset() {
		delegate.reset();
	}

	public void updateTransform(final Matrix4 emitterTrans) {
		this.absoluteTransform.set(emitterTrans);

		this.absoluteTransform.translate(this.relativeTransform.getTranslation(new Vector3()));

		this.absoluteTransform.rotate(this.relativeTransform.getRotation(new Quaternion()));

		this.absoluteTransform.scl(this.relativeTransform.getScale(new Vector3()));

		if (this.delegate != null) {
			this.delegate.setTransform(absoluteTransform);
		}
	}

	public String getParticle() {
		return particle;
	}

	public void setParticle(String particle) {
		this.particle = particle;
	}

	public Matrix4 getRelativeTransform() {
		return relativeTransform;
	}

	public void setRelativeTransform(Matrix4 transform) {
		this.relativeTransform.set(transform);
	}

	@JsonIgnore
	public Matrix4 getAbsoluteTransform() {
		return absoluteTransform;
	}

	@JsonIgnore
	public void setAbsoluteTransform(Matrix4 transform) {
		this.absoluteTransform.set(transform);
	}

	public void dispose() {
		if (this.delegate != null)
			delegate.dispose();
	}
}
