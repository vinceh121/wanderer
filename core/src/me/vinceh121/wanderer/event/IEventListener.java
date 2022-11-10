package me.vinceh121.wanderer.event;

@FunctionalInterface
public interface IEventListener {
	void handleEvent(Event e);
}
