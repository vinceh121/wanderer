package me.vinceh121.wanderer.input;

public enum Input {
	WALK_FORWARDS("Walk Forwards"),
	WALK_BACKWARDS("Walk Backwards"),
	WALK_LEFT("Walk Left"),
	WALK_RIGHT("Walk Right"),
	FLY_LEFT("Flying Left"),
	FLY_RIGHT("Flying Right"),
	FLY_BOOST("Flying Boost"),
	SWITCH_CONTROLLED_VEHICLE("Switch controlled vehicle"),
	DEBUG_BULLET("Physics Debug"),
	DEBUG_GLX("Graphics Debug"),
	PAUSE_MENU("Pause"),
	SCROLL_BELT_LEFT("Scroll Belt Left"),
	SCROLL_BELT_RIGHT("Scroll Belt Right"),
	UI_VALIDATE("UI Validate"),
	OPEN_BELT("Open Belt"),
	JUMP("Jump");

	private final String name;

	Input(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}