package me.vinceh121.wanderer.entity;

import java.util.Map;

import me.vinceh121.wanderer.combat.DamageType;

public interface ILivingEntity {
	float getMaxHealth();

	void setMaxHealth(float maxHealth);

	float getHealth();

	void setHealth(float health);

	void damage(float damage, DamageType type);

	void onDeath();

	boolean isDead();

	void setInvincible(boolean invincible);

	boolean isInvincible();

	Map<DamageType, Float> getArmors();

	float getArmor(DamageType type);

	void setArmor(DamageType type, float armor);
}
