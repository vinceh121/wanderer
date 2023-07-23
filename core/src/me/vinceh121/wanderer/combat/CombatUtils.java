package me.vinceh121.wanderer.combat;

public final class CombatUtils {
	/**
	 * In the original game this is delta time, but it messes up on higher
	 * framerates. Should we find out a way to scale DPS like the intended way or
	 * keep this this way?
	 *
	 * Set to 30fps in order to keep the intended balancing.
	 */
	private static final float COEFFICIENT = 1f / 30f;

	public static float dealtDamage(final float damage, final float armor) {
		return damage * CombatUtils.COEFFICIENT - armor * CombatUtils.COEFFICIENT;
	}
}
