package me.vinceh121.wanderer.desktop.audio;

import static org.lwjgl.openal.AL10.*;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
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
		this.setContext();
		this.source = AL10.alGenSources();
		this.audio.registerSource(this);
		OpenAL3DAudio.checkOpenALError();
	}

	@Override
	public Vector3 getPosition() {
		this.setContext();
		final float[] x = new float[1];
		final float[] y = new float[1];
		final float[] z = new float[1];
		AL10.alGetSource3f(this.source, AL10.AL_POSITION, x, y, z);
		return new Vector3(x[0], y[0], z[0]);
	}

	@Override
	public void setPosition(final Vector3 pos) {
		this.setContext();
		AL10.alSource3f(this.source, AL10.AL_POSITION, pos.x, pos.y, pos.z);
	}

	@Override
	public Vector3 getRelativePosition() {
		return this.relativePosition;
	}

	@Override
	public void setRelativePosition(final Vector3 from) {
		this.relativePosition.set(from);
	}

	@Override
	public void setRelativePosition(final float x, final float y, final float z) {
		this.relativePosition.set(x, y, z);
	}

	@Override
	public Vector3 getVelocity() {
		this.setContext();
		final float[] x = new float[1];
		final float[] y = new float[1];
		final float[] z = new float[1];
		AL10.alGetSource3f(this.source, AL10.AL_VELOCITY, x, y, z);
		return new Vector3(x[0], y[0], z[0]);
	}

	@Override
	public void setVelocity(final Vector3 vel) {
		this.setContext();
		AL10.alSource3f(this.source, AL10.AL_VELOCITY, vel.x, vel.y, vel.z);
	}

	@Override
	public boolean isLooping() {
		this.setContext();
		return AL10.alGetSourcei(this.source, AL10.AL_LOOPING) == AL10.AL_TRUE;
	}

	@Override
	public void setLooping(final boolean loop) {
		this.setContext();
		AL10.alSourcei(this.source, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	@Override
	public float getGain() {
		this.setContext();
		return AL10.alGetSourcef(this.source, AL10.AL_GAIN);
	}

	@Override
	public void setGain(final float gain) {
		this.setContext();
		AL10.alSourcef(this.source, AL10.AL_GAIN, gain);
	}

	@Override
	public int getBuffer() {
		this.setContext();
		return AL10.alGetSourcei(this.source, AL10.AL_BUFFER);
	}

	@Override
	public void setBuffer(final int buffer) {
		this.setContext();
		AL10.alSourcei(this.source, AL10.AL_BUFFER, buffer);
	}

	@Override
	public float[] getOrientation() {
		this.setContext();
		final float[] orientation = new float[6];
		AL10.alGetSourcefv(this.source, AL10.AL_VELOCITY, orientation);
		return orientation;
	}

	@Override
	public void setOrientation(final Vector3 at, final Vector3 up) {
		this.setContext();
		AL10.alSourcefv(this.source, AL10.AL_ORIENTATION, new float[] { at.x, at.y, at.z, up.x, up.y, up.z });
	}

	@Override
	public boolean isPlaying() {
		this.setContext();
		return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	@Override
	public boolean isStopped() {
		this.setContext();
		return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED;
	}

	@Override
	public boolean isPaused() {
		this.setContext();
		return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
	}

	@Override
	public void play() {
		this.setContext();
		AL10.alSourcePlay(this.source);
	}

	@Override
	public void pause() {
		this.setContext();
		AL10.alSourcePause(this.source);
	}

	@Override
	public void stop() {
		this.setContext();
		AL10.alSourceStop(this.source);
	}

	@Override
	public void rewind() {
		this.setContext();
		AL10.alSourceRewind(this.source);
	}

	@Override
	public void setPitch(final float pitch) {
		this.setContext();
		AL10.alSourcef(this.source, AL10.AL_PITCH, pitch);
	}

	@Override
	public float getPitch() {
		this.setContext();
		return AL10.alGetSourcef(this.source, AL10.AL_PITCH);
	}

	@Override
	public void setMaxDistance(final float dist) {
		this.setContext();
		AL10.alSourcef(this.source, AL10.AL_MAX_DISTANCE, dist);
	}

	@Override
	public float getMaxDistnace() {
		this.setContext();
		return AL10.alGetSourcef(this.source, AL10.AL_MAX_DISTANCE);
	}

	@Override
	public void setMinDistance(final float dist) {
		this.setContext();
		AL10.alSourcef(this.source, AL10.AL_REFERENCE_DISTANCE, dist);
	}

	@Override
	public float getMinDistance() {
		this.setContext();
		return AL10.alGetSourcef(this.source, AL10.AL_REFERENCE_DISTANCE);
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
	public void setDisposeOnStop(final boolean disposeOnStop) {
		this.disposeOnStop = disposeOnStop;
	}

	@Override
	public boolean isDisposed() {
		return this.disposed || AL10.alIsSource(this.source);
	}

	@Override
	protected void finalize() throws Throwable {
		if (this.disposed) {
			return;
		}
		OpenAL3DSource.LOG.error("Garbaging {}", this.source);
		this.dispose();
	}

	private void setContext() {
		ALC10.alcMakeContextCurrent(this.context);
	}

	@Override
	public void dispose() {
		try {
			if (this.disposed) {
				return;
			}
			this.disposed = true;
			this.setContext();
			AL10.alDeleteSources(this.source);
			OpenAL3DAudio.checkOpenALError();
		} catch (final OpenALException e) {
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

	@Override
	public String toString() {
		return "OpenAL3DSource [audio=" + this.audio + ", source=" + this.source + ", context=" + this.context + ", relativePosition="
				+ this.relativePosition + ", disposeOnStop=" + this.disposeOnStop + ", disposed=" + this.disposed + ", getPosition()="
				+ this.getPosition() + ", getVelocity()=" + this.getVelocity() + ", isLooping()=" + this.isLooping() + ", getGain()="
				+ this.getGain() + ", getBuffer()=" + this.getBuffer() + ", getOrientation()=" + Arrays.toString(this.getOrientation())
				+ ", isPlaying()=" + this.isPlaying() + ", isStopped()=" + this.isStopped() + ", isPaused()=" + this.isPaused()
				+ ", getPitch()=" + this.getPitch() + ", getMaxDistnace()=" + this.getMaxDistnace() + ", getMinDistance()="
				+ this.getMinDistance() + ", isDisposed()=" + this.isDisposed() + "]";
	}
}
