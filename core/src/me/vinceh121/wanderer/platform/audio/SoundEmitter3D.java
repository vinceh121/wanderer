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

	/**
	 * @param pos The absolute position in the OpenAL world
	 */
	void setPosition(final Vector3 pos);

	/**
	 * @return The absolute position in the OpenAL world
	 */
	Vector3 getPosition();

	/**
	 * Frequency shift by pitch, will change playback speed.
	 *
	 * @param pitch [0-1[
	 */
	void setPitch(final float pitch);

	/**
	 * Frequency shift by pitch, will change playback speed.
	 *
	 * @return pitch [0-1[
	 */
	float getPitch();

	long getId();

	void setRelativePosition(float x, float y, float z);

	void setRelativePosition(Vector3 from);

	Vector3 getRelativePosition();

	void setMaxDistance(float dist);

	float getMaxDistnace();

	void setMinDistance(float dist);

	float getMinDistance();
}
