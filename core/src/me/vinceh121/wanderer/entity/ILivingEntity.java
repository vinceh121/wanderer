package me.vinceh121.wanderer.entity;

public interface ILivingEntity {
	float getMaxHealth();
	
	void setMaxHealth(float maxHealth);
	
	float getHealth();

	void setHealth(float health);

	void damage(float health);

	void onDeath();

	void setInvincible(boolean invincible);

	boolean isInvincible();
}
