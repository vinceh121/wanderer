package me.vinceh121.wanderer.entity;

import me.vinceh121.wanderer.Wanderer;

public abstract class AbstractLivingEntity extends AbstractEntity implements ILivingEntity {
	private float health = 1;
	private boolean invincible;

	public AbstractLivingEntity(final Wanderer game) {
		super(game);
	}

	@Override
	public boolean isInvincible() {
		return this.invincible;
	}

	@Override
	public void setInvincible(final boolean invincible) {
		this.invincible = invincible;
		this.checkDeath();
	}

	@Override
	public float getHealth() {
		return this.health;
	}

	@Override
	public void setHealth(final float health) {
		this.health = health;
		this.checkDeath();
	}

	@Override
	public void damage(final float health) {
		this.health += health;
		this.checkDeath();
	}

	private void checkDeath() {
		if (this.health <= 0 && !this.invincible) {
			this.onDeath();
		}
	}
}
