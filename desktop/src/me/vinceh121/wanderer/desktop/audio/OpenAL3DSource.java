package me.vinceh121.wanderer.desktop.audio;

import static org.lwjgl.openal.AL10.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.ALC11;

import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.platform.audio.SoundEmitter3D;

public class OpenAL3DSource implements SoundEmitter3D {
	private static final Logger LOG = LogManager.getLogger(OpenAL3DAudio.class);
	private final OpenAL3DAudio audio;
	private final int source;
	private final long context;
	private final Vector3 relativePosition = new Vector3();
	private boolean disposeOnStop, disposed;

	public OpenAL3DSource(final OpenAL3DAudio audio, final long context) throws OpenALException {
		this.audio = audio;
		this.context = context;
		OpenAL3DAudio.checkOpenALError();
		setContext();
		this.source = alGenSources();
		this.audio.registerSource(this);
		OpenAL3DAudio.checkOpenALError();
	}

	@Override
	public Vector3 getPosition() {
		setContext();
		final float[] x = new float[1];
		final float[] y = new float[1];
		final float[] z = new float[1];
		alGetSource3f(this.source, AL_POSITION, x, y, z);
		return new Vector3(x[0], y[0], z[0]);
	}

	@Override
	public void setPosition(final Vector3 pos) {
		setContext();
		alSource3f(this.source, AL_POSITION, pos.x, pos.y, pos.z);
	}

	@Override
	public Vector3 getRelativePosition() {
		return this.relativePosition;
	}

	@Override
	public void setRelativePosition(Vector3 from) {
		this.relativePosition.set(from);
	}

	@Override
	public void setRelativePosition(float x, float y, float z) {
		this.relativePosition.set(x, y, z);
	}

	@Override
	public Vector3 getVelocity() {
		setContext();
		final float[] x = new float[1];
		final float[] y = new float[1];
		final float[] z = new float[1];
		alGetSource3f(this.source, AL_VELOCITY, x, y, z);
		return new Vector3(x[0], y[0], z[0]);
	}

	@Override
	public void setVelocity(final Vector3 vel) {
		setContext();
		alSource3f(this.source, AL_VELOCITY, vel.x, vel.y, vel.z);
	}

	@Override
	public boolean isLooping() {
		setContext();
		return alGetSourcei(this.source, AL_LOOPING) == AL_TRUE;
	}

	@Override
	public void setLooping(final boolean loop) {
		setContext();
		alSourcei(this.source, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
	}

	@Override
	public float getGain() {
		setContext();
		return alGetSourcef(this.source, AL_GAIN);
	}

	@Override
	public void setGain(final float gain) {
		setContext();
		alSourcef(this.source, AL_GAIN, gain);
	}

	@Override
	public int getBuffer() {
		setContext();
		return alGetSourcei(this.source, AL_BUFFER);
	}

	@Override
	public void setBuffer(int buffer) {
		setContext();
		alSourcei(this.source, AL_BUFFER, buffer);
	}

	@Override
	public float[] getOrientation() {
		setContext();
		final float[] orientation = new float[6];
		alGetSourcefv(this.source, AL_VELOCITY, orientation);
		return orientation;
	}

	@Override
	public void setOrientation(final Vector3 at, final Vector3 up) {
		setContext();
		alSourcefv(this.source, AL_ORIENTATION, new float[] { at.x, at.y, at.z, up.x, up.y, up.z });
	}

	@Override
	public boolean isPlaying() {
		setContext();
		return alGetSourcei(this.source, AL_SOURCE_STATE) == AL_PLAYING;
	}

	@Override
	public boolean isStopped() {
		setContext();
		return alGetSourcei(this.source, AL_SOURCE_STATE) == AL_STOPPED;
	}

	@Override
	public boolean isPaused() {
		setContext();
		return alGetSourcei(this.source, AL_SOURCE_STATE) == AL_PAUSED;
	}

	@Override
	public void play() {
		setContext();
		alSourcePlay(this.source);
	}

	@Override
	public void pause() {
		setContext();
		alSourcePause(this.source);
	}

	@Override
	public void stop() {
		setContext();
		alSourceStop(this.source);
	}

	@Override
	public void rewind() {
		setContext();
		alSourceRewind(this.source);
	}

	@Override
	public void setPitch(float pitch) {
		setContext();
		alSourcef(this.source, AL_PITCH, pitch);
	}

	@Override
	public float getPitch() {
		setContext();
		return alGetSourcef(this.source, AL_PITCH);
	}

	@Override
	public void setMaxDistance(float dist) {
		setContext();
		alSourcef(this.source, AL_MAX_DISTANCE, dist);
	}

	@Override
	public float getMaxDistnace() {
		setContext();
		return alGetSourcef(this.source, AL_MAX_DISTANCE);
	}

	@Override
	public void setMinDistance(float dist) {
		setContext();
		alSourcef(this.source, AL_REFERENCE_DISTANCE, dist);
	}

	@Override
	public float getMinDistance() {
		setContext();
		return alGetSourcef(this.source, AL_REFERENCE_DISTANCE);
	}

	@Override
	public void update() {
		if (!this.isDisposed() && this.isDisposeOnStop() && this.isStopped()) {
			this.dispose();
		}
	}

	@Override
	public boolean isDisposeOnStop() {
		return this.disposeOnStop;
	}

	@Override
	public void setDisposeOnStop(boolean disposeOnStop) {
		this.disposeOnStop = disposeOnStop;
	}

	@Override
	public boolean isDisposed() {
		return this.disposed;
	}

	@Override
	protected void finalize() throws Throwable {
		if (this.disposed) {
			return;
		}
		LOG.error("Garbaging {}", this.source);
		this.dispose();
	}

	private void setContext() {
		ALC11.alcMakeContextCurrent(this.context);
	}

	@Override
	public void dispose() {
		try {
			if (this.disposed) {
				return;
			}
			setContext();
			this.disposed = true;
			this.stop();
			this.setBuffer(0);
			alDeleteSources(this.source);
			OpenAL3DAudio.checkOpenALError();
		} catch (OpenALException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
	}

	@Override
	public long getId() {
		return this.source;
	}
}
