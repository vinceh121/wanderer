package me.vinceh121.wanderer.desktop.audio;

import java.nio.ByteBuffer;

import org.lwjgl.openal.AL10;

import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.platform.audio.SoundEmitter3D;

public class OpenAL3DBuffer implements Sound3D {
	private final OpenAL3DAudio audio;
	private final int buffer;
	private boolean disposed;

	public OpenAL3DBuffer(final OpenAL3DAudio audio) throws OpenALException {
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
		return this.playSource3D(1, this.audio.getListenerPosition()).getId();
	}

	@Override
	public long play(final float volume) {
		return this.playSource3D(volume).getId();
	}

	@Override
	public long play(final float volume, final float pitch, final float pan) {
		throw new UnsupportedOperationException("OpenAL3DBuffer#play(volume, pitch, pan) is not implemented");
	}

	public SoundEmitter3D playSource(final long context) {
		try {
			final OpenAL3DSource src = new OpenAL3DSource(this.audio, context);
			src.setBuffer(this.buffer);
			src.setMinDistance(10); // TODO figure out the best defaults
			src.play();
			OpenAL3DAudio.runtimeCheckOpenAlError();
			return src;
		} catch (final OpenALException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public SoundEmitter3D playGeneral() {
		return this.playSource(this.audio.getContextGeneral());
	}

	@Override
	public SoundEmitter3D playSource3D() {
		return this.playSource(this.audio.getContext3D());
	}

	@Override
	public SoundEmitter3D playSource3D(final float volume) {
		final SoundEmitter3D src = this.playSource3D();
		src.setGain(volume);
		OpenAL3DAudio.runtimeCheckOpenAlError();
		return src;
	}

	@Override
	public SoundEmitter3D playSource3D(final float volume, final Vector3 position) {
		final SoundEmitter3D src = this.playSource3D(volume);
		src.setPosition(position);
		OpenAL3DAudio.runtimeCheckOpenAlError();
		return src;
	}

	@Override
	public long loop() {
		throw new UnsupportedOperationException("OpenAL3DBuffer#loop() is not implemented");
	}

	@Override
	public long loop(final float volume) {
		throw new UnsupportedOperationException("OpenAL3DBuffer#loop(volume) is not implemented");
	}

	@Override
	public long loop(final float volume, final float pitch, final float pan) {
		throw new UnsupportedOperationException("OpenAL3DBuffer#loop(volume, pitch, pan) is not implemented");
	}

	@Override
	public void stop() {
		throw new UnsupportedOperationException("OpenAL3DBuffer#stop() is not implemented");
	}

	@Override
	public void pause() {
		throw new UnsupportedOperationException("OpenAL3DBuffer#pause() is not implemented");
	}

	@Override
	public void resume() {
		throw new UnsupportedOperationException("OpenAL3DBuffer#resume() is not implemented");
	}

	@Override
	public void stop(final long soundId) {
		throw new UnsupportedOperationException("OpenAL3DBuffer#stop(soundId) is not implemented");
	}

	@Override
	public void pause(final long soundId) {
		throw new UnsupportedOperationException("OpenAL3DBuffer#pause(soundId) is not implemented");
	}

	@Override
	public void resume(final long soundId) {
		throw new UnsupportedOperationException("OpenAL3DBuffer#resume(soundId) is not implemented");
	}

	@Override
	public void setLooping(final long soundId, final boolean looping) {
		throw new UnsupportedOperationException("OpenAL3DBuffer#setLooping(soundId, looping) is not implemented");
	}

	@Override
	public void setPitch(final long soundId, final float pitch) {
		throw new UnsupportedOperationException("OpenAL3DBuffer#setPitch(soundId, pitch) is not implemented");
	}

	@Override
	public void setVolume(final long soundId, final float volume) {
		throw new UnsupportedOperationException("OpenAL3DBuffer#setVolume(soundId, volume) is not implemented");
	}

	@Override
	public void setPan(final long soundId, final float pan, final float volume) {
		throw new UnsupportedOperationException("OpenAL3DBuffer#setPan(soundId, pan, volume) is not implemented");
	}

	@Override
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
			this.audio.disposeBuffer(this.buffer);
		} catch (final OpenALException e) {
			throw new RuntimeException(e);
		}
	}
}
