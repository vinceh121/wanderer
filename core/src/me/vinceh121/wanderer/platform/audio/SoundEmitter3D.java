package me.vinceh121.wanderer.platform.audio;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public interface SoundEmitter3D extends Disposable {

	boolean isDisposed();

	void setDisposeOnStop(boolean disposeOnStop);

	boolean isDisposeOnStop();

	void update();

	void rewind();

	void stop();

	void pause();

	void play();

	boolean isPaused();

	boolean isStopped();

	boolean isPlaying();

	void setOrientation(final Vector3 at, final Vector3 up);

	float[] getOrientation();

	void setBuffer(int buffer);

	int getBuffer();

	void setGain(final float gain);

	float getGain();

	void setLooping(final boolean loop);

	boolean isLooping();

	void setVelocity(final Vector3 vel);

	Vector3 getVelocity();

	void setPosition(final Vector3 pos);

	Vector3 getPosition();

	long getId();

}
