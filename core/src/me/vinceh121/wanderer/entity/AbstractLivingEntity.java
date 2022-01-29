package me.vinceh121.wanderer.entity;

import me.vinceh121.wanderer.Wanderer;

public abstract class AbstractLivingEntity extends AbstractEntity implements ILivingEntity {
	private float health = 1;
	private boolean invincible;

	public AbstractLivingEntity(Wanderer game) {
		super(game);
	}

	@Override
	public boolean isInvincible() {
		return invincible;
	}

	@Override
	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
		this.checkDeath();
	}

	@Override
	public float getHealth() {
		return this.health;
	}

	@Override
	public void setHealth(float health) {
		this.health = health;
		this.checkDeath();
	}

	@Override
	public void damage(float health) {
		this.health += health;
		this.checkDeath();
	}

	private void checkDeath() {
		if (this.health <= 0 && !this.invincible) {
			this.onDeath();
		}
	}
}
