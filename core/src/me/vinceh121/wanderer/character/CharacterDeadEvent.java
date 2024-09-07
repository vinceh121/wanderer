package me.vinceh121.wanderer.character;

import me.vinceh121.wanderer.event.Event;

public class CharacterDeadEvent extends Event {
	public static final String TYPE = "characterDead";
	private final CharacterW character;

	public CharacterDeadEvent(final CharacterW character) {
		super(TYPE);

		this.character = character;
	}

	public CharacterW getCharacter() {
		return character;
	}
}
