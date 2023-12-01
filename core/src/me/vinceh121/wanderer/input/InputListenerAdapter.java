package me.vinceh121.wanderer.input;

public class InputListenerAdapter implements InputListener {
	private final int priority;

	/**
	 * Priorities typical ranges are:
	 * <ul>
	 * <li>0 : general, available all the time in game</li>
	 * <li>50 : currently controlled character/gun tower/plane</li>
	 * <li>-1000 : NOOP</li>
	 * </ul>
	 *
	 * @param priority
	 */
	public InputListenerAdapter(final int priority) {
		this.priority = priority;
	}

	@Override
	public boolean inputDown(final Input in) {
		return false;
	}

	@Override
	public boolean inputUp(final Input in) {
		return false;
	}

	@Override
	public boolean mouseMoved(final int x, final int y) {
		return false;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}
}
