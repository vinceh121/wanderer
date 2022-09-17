package me.vinceh121.wanderer.input;

public class InputListenerAdapter implements InputListener {
	private final int priority;

	public InputListenerAdapter(final int priority) {
		this.priority = priority;
	}

	@Override
	public boolean inputDown(Input in) {
		return false;
	}

	@Override
	public boolean inputUp(Input in) {
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		return false;
	}

	@Override
	public int getPriority() {
		return priority;
	}
}
