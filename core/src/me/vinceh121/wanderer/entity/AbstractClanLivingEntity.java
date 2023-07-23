package me.vinceh121.wanderer.entity;

import java.util.EnumMap;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.combat.CombatUtils;
import me.vinceh121.wanderer.combat.DamageType;

public abstract class AbstractClanLivingEntity extends AbstractEntity implements ILivingEntity, IClanMember {
	private final Map<DamageType, Float> armors = new EnumMap<>(DamageType.class);
	private float maxHealth = 1, health = 1;
	private boolean invincible;
	private Clan clan;

	public AbstractClanLivingEntity(final Wanderer game) {
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
	public float getMaxHealth() {
		return this.maxHealth;
	}

	@Override
	public void setMaxHealth(final float maxHealth) {
		this.maxHealth = maxHealth;
		this.setHealth(Math.min(this.maxHealth, this.health));
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
	public void damage(final float damage, final DamageType type) {
		if (this.isInvincible()) {
			return;
		}

		final float armor = this.getArmor(type);
		final float dealtDamage = CombatUtils.dealtDamage(damage, armor);

		this.health -= dealtDamage;

		this.checkDeath();
	}

	@Override
	public Clan getClan() {
		return this.clan;
	}

	@Override
	public void onJoinClan(final Clan clan) {
		this.clan = clan;
	}

	@Override
	public Map<DamageType, Float> getArmors() {
		return this.armors;
	}

	@Override
	public float getArmor(final DamageType type) {
		return this.armors.containsKey(type) ? this.armors.get(type) : 0;
	}

	@Override
	public void setArmor(final DamageType type, final float armor) {
		this.armors.put(type, armor);
	}

	@Override
	public void writeState(final ObjectNode node) {
		super.writeState(node);
		if (this.getClan() != null) {
			node.put("clan", this.getClan().getId().getValue());
		} else {
			node.putNull("clan");
		}
		node.put("maxHealth", this.getMaxHealth());
		node.put("health", this.getHealth());
		node.put("invincible", this.isInvincible());
	}

	@Override
	public void readState(final ObjectNode node) {
		super.readState(node);
		if (node.hasNonNull("clan")) {
			this.onJoinClan(this.game.getClan(node.get("clan").asInt()));
		}
		this.setMaxHealth(node.get("maxHealth").floatValue());
		this.setHealth(node.get("health").floatValue());
		this.setInvincible(node.get("invincible").asBoolean());
	}

	protected void checkDeath() {
		if (this.health <= 0 && !this.invincible) {
			this.onDeath();
		}
	}

	@Override
	public void dispose() {
		if (this.clan != null) {
			this.clan.removeMember(this);
		}
		super.dispose();
	}
}
