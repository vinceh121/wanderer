package me.vinceh121.wanderer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import me.vinceh121.wanderer.ID;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;

public abstract class AbstractClanLivingEntity extends AbstractEntity implements ILivingEntity, IClanMember {
	private float health = 1;
	private boolean invincible;
	private ID clanId;

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
	
	public ID getClanId() {
		return this.clanId;
	}
	
	@Override
	@JsonIgnore
	public Clan getClan() {
		return this.game.getClan(this.clanId);
	}
	
	@Override
	public void onJoinClan(Clan clan) {
		this.clanId = clan.getId();
	}

	protected void checkDeath() {
		if (this.health <= 0 && !this.invincible) {
			this.onDeath();
		}
	}
}
