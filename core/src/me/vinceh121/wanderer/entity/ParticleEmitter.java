package me.vinceh121.wanderer.entity;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.math.Matrix4;
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
		return this.delegate;
	}

	public void setDelegate(final ParticleEffect delegate) {
		this.delegate = delegate;
	}

	public void updateLoading() {
		if (this.particle != null && this.delegate == null) {
			if (WandererConstants.ASSET_MANAGER.isLoaded(this.particle, ParticleEffect.class)) {
				this.delegate = new ParticleEffect(
						WandererConstants.ASSET_MANAGER.get(this.particle, ParticleEffect.class));
				this.delegate.init();
				this.delegate.start();
				this.delegate.setTransform(this.absoluteTransform);
				this.system.add(this.delegate);
			} else {
				final ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(
						this.system.getBatches());
				WandererConstants.ASSET_MANAGER.load(this.particle, ParticleEffect.class, loadParam);
			}
		}
	}

	public void reset() {
		this.delegate.reset();
	}

	public void updateTransform(final Matrix4 emitterTrans) {
		this.absoluteTransform.set(emitterTrans);

		this.absoluteTransform.mul(this.relativeTransform);

		if (this.delegate != null) {
			this.delegate.setTransform(this.absoluteTransform);
		}
	}

	public String getParticle() {
		return this.particle;
	}

	public void setParticle(final String particle) {
		this.particle = particle;
	}

	public Matrix4 getRelativeTransform() {
		return this.relativeTransform;
	}

	public void setRelativeTransform(final Matrix4 transform) {
		this.relativeTransform.set(transform);
	}

	@JsonIgnore
	public Matrix4 getAbsoluteTransform() {
		return this.absoluteTransform;
	}

	@JsonIgnore
	public void setAbsoluteTransform(final Matrix4 transform) {
		this.absoluteTransform.set(transform);
	}

	public void dispose() {
		if (this.delegate != null) {
			this.delegate.dispose();
		}
	}
}
