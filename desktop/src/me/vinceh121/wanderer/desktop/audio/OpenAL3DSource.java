package me.vinceh121.wanderer.desktop.audio;

import static org.lwjgl.openal.AL10.*;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class OpenAL3DSource implements Disposable {
	private final OpenAL3DAudio audio;
	private final int source;
	private boolean disposeOnStop = true, disposed;

	public OpenAL3DSource(final OpenAL3DAudio audio) throws OpenALException {
		this.audio = audio;
		this.source = alGenSources();
		this.audio.registerSource(this);
	}

	public Vector3 getPosition() {
		final float[] x = new float[1];
		final float[] y = new float[1];
		final float[] z = new float[1];
		alGetSource3f(this.source, AL_POSITION, x, y, z);
		return new Vector3(x[0], y[0], z[0]);
	}

	public void setPosition(final Vector3 pos) {
		alSource3f(this.source, AL_POSITION, pos.x, pos.y, pos.z);
	}

	public Vector3 getVelocity() {
		final float[] x = new float[1];
		final float[] y = new float[1];
		final float[] z = new float[1];
		alGetSource3f(this.source, AL_VELOCITY, x, y, z);
		return new Vector3(x[0], y[0], z[0]);
	}

	public void setVelocity(final Vector3 vel) {
		alSource3f(this.source, AL_VELOCITY, vel.x, vel.y, vel.z);
	}

	public boolean isLooping() {
		return alGetSourcei(this.source, AL_LOOPING) == AL_TRUE;
	}

	public void setLooping(final boolean loop) {
		alSourcei(this.source, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
	}

	public float getGain() {
		return alGetSourcef(this.source, AL_GAIN);
	}

	public void setGain(final float gain) {
		alSourcef(this.source, AL_GAIN, gain);
	}

	public int getBuffer() {
		return alGetSourcei(this.source, AL_BUFFER);
	}

	public void setBuffer(int buffer) {
		alSourcei(this.source, AL_BUFFER, buffer);
	}

	public float[] getOrientation() {
		final float[] orientation = new float[6];
		alGetSourcefv(this.source, AL_VELOCITY, orientation);
		return orientation;
	}

	public void setOrientation(final Vector3 at, final Vector3 up) {
		alSourcefv(this.source, AL_ORIENTATION, new float[] { at.x, at.y, at.z, up.x, up.y, up.z });
	}

	public boolean isPlaying() {
		return alGetSourcei(this.source, AL_SOURCE_STATE) == AL_PLAYING;
	}

	public boolean isStopped() {
		return alGetSourcei(this.source, AL_SOURCE_STATE) == AL_STOPPED;
	}

	public boolean isPaused() {
		return alGetSourcei(this.source, AL_SOURCE_STATE) == AL_PAUSED;
	}

	public void play() {
		alSourcePlay(this.source);
	}

	public void pause() {
		alSourcePause(this.source);
	}

	public void stop() {
		alSourceStop(this.source);
	}

	public void rewind() {
		alSourceRewind(this.source);
	}

	public void update() {
		if (!this.isDisposed() && this.isDisposeOnStop() && this.isStopped()) {
			this.dispose();
		}
	}

	public boolean isDisposeOnStop() {
		return this.disposeOnStop;
	}

	public void setDisposeOnStop(boolean disposeOnStop) {
		this.disposeOnStop = disposeOnStop;
	}

	public boolean isDisposed() {
		return this.disposed;
	}

	@Override
	public void dispose() {
		try {
			if (this.disposed) {
				return;
			}
			this.disposed = true;
			this.setBuffer(0);
			alDeleteSources(this.source);
			OpenAL3DAudio.checkOpenALError();
		} catch (OpenALException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int hashCode() {
		return this.getSource();
	}

	public int getSource() {
		return this.source;
	}
}
