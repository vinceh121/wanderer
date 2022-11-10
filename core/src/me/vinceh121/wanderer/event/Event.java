package me.vinceh121.wanderer.event;

public class Event {
	private final IEventType type;
	private boolean cancelable, cancelled;

	public Event(final IEventType type) {
		this.type = type;
	}

	public boolean isCancelable() {
		return this.cancelable;
	}

	public void setCancelable(final boolean cancelable) {
		this.cancelable = cancelable;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(final boolean cancelled) {
		if (!this.isCancelable()) {
			throw new IllegalStateException("Cannot cancel an event that's not cancellable");
		}
		this.cancelled = cancelled;
	}

	public IEventType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "Event [type=" + this.type + ", cancelable=" + this.cancelable + ", cancelled=" + this.cancelled + "]";
	}
}
