package me.vinceh121.wanderer;

import com.badlogic.gdx.ApplicationAdapter;

public abstract class ApplicationDelegate extends ApplicationAdapter {
	protected final ApplicationMultiplexer applicationMultiplexer;

	public ApplicationDelegate(final ApplicationMultiplexer applicationMultiplexer) {
		this.applicationMultiplexer = applicationMultiplexer;
	}
}
