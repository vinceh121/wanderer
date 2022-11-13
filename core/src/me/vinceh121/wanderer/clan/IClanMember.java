package me.vinceh121.wanderer.clan;

import com.fasterxml.jackson.annotation.JsonIgnore;

import me.vinceh121.wanderer.ID;

public interface IClanMember {
	ID getId();

	@JsonIgnore
	Clan getClan();

	/**
	 * Do not call, to add a member to a clan use
	 * {@link Clan#addMember(me.vinceh121.wanderer.entity.ILivingEntity)}
	 */
	void onJoinClan(Clan clan);
}
