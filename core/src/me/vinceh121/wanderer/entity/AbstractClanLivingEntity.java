package me.vinceh121.wanderer.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;

public abstract class AbstractClanLivingEntity extends AbstractEntity implements ILivingEntity, IClanMember {
	private float health = 1;
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

	@Override
	public Clan getClan() {
		return this.clan;
	}

	@Override
	public void onJoinClan(Clan clan) {
		this.clan = clan;
	}

	@Override
	public void writeState(ObjectNode node) {
		super.writeState(node);
		if (this.getClan() != null) {
			node.put("clan", this.getClan().getId().getValue());
		} else {
			node.putNull("clan");
		}
		node.put("health", this.getHealth());
		node.put("invincible", this.isInvincible());
	}

	@Override
	public void readState(ObjectNode node) {
		super.readState(node);
		if (node.hasNonNull("clan")) {
			this.onJoinClan(this.game.getClan(node.get("clan").asInt()));
		}
		this.setHealth(node.get("health").floatValue());
		this.setInvincible(node.get("invincible").asBoolean());
	}

	protected void checkDeath() {
		if (this.health <= 0 && !this.invincible) {
			this.onDeath();
		}
	}
}
