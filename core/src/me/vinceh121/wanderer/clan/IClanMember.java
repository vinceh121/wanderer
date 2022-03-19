package me.vinceh121.wanderer.clan;

public interface IClanMember {
	Clan getClan();

	/**
	 * Do not call, to add a member to a clan use
	 * {@link Clan#addMember(me.vinceh121.wanderer.entity.ILivingEntity)}
	 */
	void onJoinClan(Clan clan);
}
