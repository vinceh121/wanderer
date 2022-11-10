package me.vinceh121.wanderer.event;

import java.util.Collection;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

public class EventDispatcher {
	/**
	 * Key: event type
	 *
	 * Value: listener
	 */
	private final MultiValuedMap<IEventType, IEventListener> listeners = new ArrayListValuedHashMap<>();

	public void addEventListener(IEventType type, IEventListener l) {
		this.listeners.put(type, l);
	}

	public void dispatchEvent(final Event e) {
		final Collection<IEventListener> lis = this.listeners.get(e.getType());
		for (final IEventListener l : lis) {
			l.handleEvent(e);
		}
	}

	public void removeEventListener(IEventType type, IEventListener l) {
		this.listeners.removeMapping(type, l);
	}

	public MultiValuedMap<IEventType, IEventListener> getListeners() {
		return listeners;
	}
}
