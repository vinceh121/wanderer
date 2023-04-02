package me.vinceh121.wanderer.input;

public enum Input {
	WALK_FORWARDS("Walk Forwards"),
	WALK_BACKWARDS("Walk Backwards"),
	WALK_LEFT("Walk Left"),
	WALK_RIGHT("Walk Right"),
	FLY_LEFT("Flying Left"),
	FLY_RIGHT("Flying Right"),
	FLY_BOOST("Flying Boost"),
	FIRE("Fire"),
	SWITCH_CONTROLLED_VEHICLE("Switch controlled vehicle"),
	DEBUG_BULLET("Physics Debug"),
	DEBUG_GLX("Graphics Debug"),
	DEBUG_TIMESCALE("Toggle Timescale"),
	CURSOR_CAPTURE("Toggle cursor capture"),
	PAUSE_MENU("Pause"),
	SHOW_OBJECTIVES("Show objectives"),
	SCROLL_BELT_LEFT("Scroll Belt Left"),
	SCROLL_BELT_RIGHT("Scroll Belt Right"),
	UI_VALIDATE("UI Validate"),
	OPEN_BELT("Open Belt"),
	JUMP("Jump"),
	QUICK_SAVE("Quick Save"),
	QUICK_LOAD("Quick Load");

	private final String name;

	Input(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
