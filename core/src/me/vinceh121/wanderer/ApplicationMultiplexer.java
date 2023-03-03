package me.vinceh121.wanderer;

import com.badlogic.gdx.ApplicationListener;

public class ApplicationMultiplexer implements ApplicationListener {
	private ApplicationListener delegate;

	public ApplicationMultiplexer() {
		this(null);
	}

	public ApplicationMultiplexer(ApplicationListener initialDelegate) {
		this.delegate = initialDelegate;
	}

	@Override
	public void create() {
		if (this.delegate != null)
			delegate.create();
	}

	@Override
	public void resize(int width, int height) {
		if (this.delegate != null)
			delegate.resize(width, height);
	}

	@Override
	public void render() {
		if (this.delegate != null)
			delegate.render();
	}

	@Override
	public void pause() {
		if (this.delegate != null)
			delegate.pause();
	}

	@Override
	public void resume() {
		if (this.delegate != null)
			delegate.resume();
	}

	@Override
	public void dispose() {
		if (this.delegate != null)
			delegate.dispose();
	}

	public ApplicationListener getDelegate() {
		return delegate;
	}

	public void setDelegate(ApplicationListener delegate) {
		this.delegate = delegate;
	}
}
