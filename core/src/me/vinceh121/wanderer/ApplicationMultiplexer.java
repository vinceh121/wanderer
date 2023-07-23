package me.vinceh121.wanderer;

import com.badlogic.gdx.ApplicationListener;

public class ApplicationMultiplexer implements ApplicationListener {
	private ApplicationListener delegate;

	public ApplicationMultiplexer() {
		this(null);
	}

	public ApplicationMultiplexer(final ApplicationListener initialDelegate) {
		this.delegate = initialDelegate;
	}

	@Override
	public void create() {
		if (this.delegate != null) {
			this.delegate.create();
		}
	}

	@Override
	public void resize(final int width, final int height) {
		if (this.delegate != null) {
			this.delegate.resize(width, height);
		}
	}

	@Override
	public void render() {
		if (this.delegate != null) {
			this.delegate.render();
		}
	}

	@Override
	public void pause() {
		if (this.delegate != null) {
			this.delegate.pause();
		}
	}

	@Override
	public void resume() {
		if (this.delegate != null) {
			this.delegate.resume();
		}
	}

	@Override
	public void dispose() {
		if (this.delegate != null) {
			this.delegate.dispose();
		}
	}

	public ApplicationListener getDelegate() {
		return this.delegate;
	}

	public void setDelegate(final ApplicationListener delegate) {
		this.delegate = delegate;
	}
}
