package me.vinceh121.wanderer.desktop.audio;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.audio.JavaSoundAudioRecorder;
import com.badlogic.gdx.backends.lwjgl3.audio.Lwjgl3Audio;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.desktop.audio.formats.AudioLoader;
import me.vinceh121.wanderer.desktop.audio.formats.OggLoader;
import me.vinceh121.wanderer.desktop.audio.formats.PcmData;
import me.vinceh121.wanderer.desktop.audio.formats.WavLoader;
import me.vinceh121.wanderer.platform.audio.AudioSystem3D;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.platform.audio.SoundEmitter3D;

public class OpenAL3DAudio implements Lwjgl3Audio, AudioSystem3D {
	private static final Map<String, AudioLoader> AUDIO_LOADERS = new HashMap<>();
	private final long device, context3D;
	private final Set<Integer> bufferPool = new HashSet<>();
	private final Set<OpenAL3DSource> sourcePool = new HashSet<>();

	public OpenAL3DAudio() throws OpenALException {
		AL.setCurrentProcess(null);
		this.device = alcOpenDevice((ByteBuffer) null);
		if (this.device == 0) {
			throw new IllegalStateException("No audio output");
		}
		final ALCCapabilities alcCap = ALC.createCapabilities(this.device);

		this.context3D = alcCreateContext(this.device, (IntBuffer) null);
		if (this.context3D == 0) {
			throw new IllegalStateException("alcCreateContext failed");
		}
		if (!alcMakeContextCurrent(this.context3D)) {
			throw new IllegalStateException("alcMakeContextCurrent failed");
		}
		AL.createCapabilities(alcCap);

		OpenAL3DAudio.checkOpenALError();
		this.checkALCError();

		this.setListenerVelocity(Vector3.Zero);
		this.setListenerPosition(Vector3.Zero);
		this.setListenerOrientation(new Vector3(0.0f, 0.0f, -1.0f), Vector3.Y);

		OpenAL3DAudio.checkOpenALError();
		this.checkALCError();
	}

	@Override
	public void setListenerPosition(final Vector3 pos) {
		alListener3f(AL_POSITION, pos.x, pos.y, pos.z);
	}

	@Override
	public Vector3 getListenerPosition() {
		final float[] x = new float[1];
		final float[] y = new float[1];
		final float[] z = new float[1];
		alGetListener3f(AL_POSITION, x, y, z);
		return new Vector3(x[0], y[0], z[0]);
	}

	@Override
	public void setListenerVelocity(final Vector3 vel) {
		alListener3f(AL_VELOCITY, vel.x, vel.y, vel.z);
	}

	@Override
	public void setListenerOrientation(final Vector3 at, final Vector3 up) {
		alListenerfv(AL_ORIENTATION, new float[] { at.x, at.y, at.z, up.x, up.y, up.z });
	}

	@Override
	public AudioDevice newAudioDevice(final int samplingRate, final boolean isMono) {
		throw new UnsupportedOperationException("newAudioDevice() is not implemented");
	}

	@Override
	public AudioRecorder newAudioRecorder(final int samplingRate, final boolean isMono) {
		return new JavaSoundAudioRecorder(samplingRate, isMono);
	}

	@Override
	public Sound3D newSound3D(final FileHandle fileHandle) {
		try (final InputStream in = fileHandle.read()) {
			final OpenAL3DBuffer sound = new OpenAL3DBuffer(this);
			final AudioLoader loader = OpenAL3DAudio.AUDIO_LOADERS.get(fileHandle.extension());
			final PcmData pcm = loader.readPCM(in);

			// Need a direct buffer, with correct byte-order, so OpenAL can read it like
			// native memory properly
			final ByteBuffer data = ByteBuffer.allocateDirect(pcm.getData().length);
			data.order(ByteOrder.nativeOrder());
			data.put(pcm.getData());
			data.flip();

			sound.loadBuffer(data, pcm.getFormat(), pcm.getSampleRate());
			OpenAL3DAudio.checkOpenALError();
			this.checkALCError();
			return sound;
		} catch (IOException | OpenALException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Sound newSound(final FileHandle fileHandle) {
		return this.newSound3D(fileHandle);
	}

	@Override
	public Music newMusic(final FileHandle file) {
		return null;
	}

	public int acquireNewBuffer() throws OpenALException {
		final int newBuf = alGenBuffers();
		OpenAL3DAudio.checkOpenALError();
		this.bufferPool.add(newBuf);
		return newBuf;
	}

	public void disposeBuffer(final int buffer) throws OpenALException {
		this.bufferPool.remove(buffer);
		for (final OpenAL3DSource src : this.sourcePool) {
			if (!src.isDisposed() && src.getBuffer() == buffer) {
				src.stop();
				src.dispose();
			}
		}
		alDeleteBuffers(buffer);
		OpenAL3DAudio.checkOpenALError();
	}

	public void registerSource(final OpenAL3DSource source) {
		this.sourcePool.add(source);
	}

	public void unregisterSource(final SoundEmitter3D source) {
		this.sourcePool.remove(source);
	}

	public void set3DContext() {
		alcMakeContextCurrent(this.context3D);
	}

	@Override
	public void update() {
		Set<OpenAL3DSource> toRemove = new HashSet<>();
		for (OpenAL3DSource src : this.sourcePool) {
			if (src.isDisposed()) {
				toRemove.add(src);
				continue;
			}
			src.update();
		}
		this.sourcePool.removeAll(toRemove);
	}

	public void checkALCError() throws OpenALException {
		final int err = alcGetError(this.device);
		if (err != ALC_NO_ERROR) {
			throw new RuntimeException(alGetString(err));
		}
	}

	@Override
	public void dispose() {
		for (OpenAL3DSource src : this.sourcePool) {
			src.dispose();
		}
		alcMakeContextCurrent(0);
		alcDestroyContext(this.context3D);
		alcCloseDevice(this.device);
	}

	public static void checkOpenALError() throws OpenALException {
		final int err = alGetError();
		if (err != AL_NO_ERROR) {
			throw new OpenALException(err);
		}
	}

	public static void runtimeCheckOpenAlError() {
		try {
			checkOpenALError();
		} catch (OpenALException e) {
			throw new RuntimeException(e);
		}
	}

	public static String sourceStateToString(final int srcState) {
		switch (srcState) {
		case AL_PLAYING:
			return "AL_PLAYING";
		case AL_STOPPED:
			return "AL_STOPPED";
		default:
			return "uhoh " + srcState;
		}
	}

	static {
		OpenAL3DAudio.AUDIO_LOADERS.put("ogg", new OggLoader());
		OpenAL3DAudio.AUDIO_LOADERS.put("wav", new WavLoader());
	}
}
