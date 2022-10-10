package me.vinceh121.wanderer.desktop.audio;

import java.nio.ByteBuffer;

import org.lwjgl.openal.AL10;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;

public class OpenAL3DSound implements Sound {
	private final OpenAL3DAudio audio;
	private final int buffer;

	public OpenAL3DSound(final OpenAL3DAudio audio) throws OpenALException {
		this.audio = audio;
		this.buffer = audio.acquireNewBuffer();
	}

	/**
	 * @param pcm
	 * @param format
	 * @param freq   in Hertz
	 * @throws OpenALException
	 */
	public void loadBuffer(final ByteBuffer pcm, final int format, final int freq) throws OpenALException {
		AL10.alBufferData(this.buffer, format, pcm.asShortBuffer(), freq);
		OpenAL3DAudio.checkOpenALError();
	}

	@Override
	public long play() {
		try {
			return this.play(1, this.audio.getListenerPosition());
		} catch (final OpenALException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public long play(final float volume) {
		throw new UnsupportedOperationException("play() is not implemented");
	}

	@Override
	public long play(final float volume, final float pitch, final float pan) {
		throw new UnsupportedOperationException("play() is not implemented");
	}

	public int play(final float volume, final Vector3 position) throws OpenALException {
		final int src = this.audio.acquireNewSource();
		AL10.alSourcei(src, AL10.AL_BUFFER, this.buffer);
		AL10.alSourcei(src, AL10.AL_LOOPING, AL10.AL_FALSE);
		AL10.alSourcef(src, AL10.AL_GAIN, volume);
		AL10.alSource3f(src, AL10.AL_POSITION, position.x, position.y, position.z);
		AL10.alSourcePlay(src);
		OpenAL3DAudio.checkOpenALError();
		return src;
	}

	@Override
	public long loop() {
		throw new UnsupportedOperationException("Calls must specify source ID");
	}

	@Override
	public long loop(final float volume) {
		throw new UnsupportedOperationException("Calls must specify source ID");
	}

	@Override
	public long loop(final float volume, final float pitch, final float pan) {
		throw new UnsupportedOperationException("Calls must specify source ID");
	}

	@Override
	public void stop() {
		throw new UnsupportedOperationException("Calls must specify source ID");
	}

	@Override
	public void pause() {
		throw new UnsupportedOperationException("Calls must specify source ID");
	}

	@Override
	public void resume() {
		throw new UnsupportedOperationException("Calls must specify source ID");
	}

	@Override
	public void dispose() {
	}

	@Override
	public void stop(final long soundId) {
	}

	@Override
	public void pause(final long soundId) {
	}

	@Override
	public void resume(final long soundId) {
	}

	@Override
	public void setLooping(final long soundId, final boolean looping) {
	}

	@Override
	public void setPitch(final long soundId, final float pitch) {
	}

	@Override
	public void setVolume(final long soundId, final float volume) {
	}

	@Override
	public void setPan(final long soundId, final float pan, final float volume) {
	}
}
