package me.vinceh121.wanderer.event;

public class Event {
	private final IEventType type;
	private boolean cancelable, cancelled;

	public Event(IEventType type) {
		this.type = type;
	}

	public boolean isCancelable() {
		return cancelable;
	}

	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		if (!this.isCancelable()) {
			throw new IllegalStateException("Cannot cancel an event that's not cancellable");
		}
		this.cancelled = cancelled;
	}

	public IEventType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Event [type=" + type + ", cancelable=" + cancelable + ", cancelled=" + cancelled + "]";
	}
}
