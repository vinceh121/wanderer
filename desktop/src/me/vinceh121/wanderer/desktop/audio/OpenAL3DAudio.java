package me.vinceh121.wanderer.desktop.audio;

import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_STOPPED;

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
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
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

public class OpenAL3DAudio implements Lwjgl3Audio {
	private static final Map<String, AudioLoader> AUDIO_LOADERS = new HashMap<>();
	private final long device, context3D;
	private final Set<Integer> bufferPool = new HashSet<>(), sourcePool = new HashSet<>();

	public OpenAL3DAudio() throws OpenALException {
		AL.setCurrentProcess(null);
		this.device = ALC10.alcOpenDevice((ByteBuffer) null);
		if (this.device == 0) {
			throw new IllegalStateException("No audio output");
		}
		final ALCCapabilities alcCap = ALC.createCapabilities(this.device);

		this.context3D = ALC10.alcCreateContext(this.device, (IntBuffer) null);
		if (this.context3D == 0) {
			throw new IllegalStateException("alcCreateContext failed");
		}
		if (!ALC10.alcMakeContextCurrent(this.context3D)) {
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

	public void setListenerPosition(final Vector3 pos) {
		AL10.alListener3f(AL10.AL_POSITION, pos.x, pos.y, pos.z);
	}

	public Vector3 getListenerPosition() {
		final float[] x = new float[1];
		final float[] y = new float[1];
		final float[] z = new float[1];
		AL10.alGetListener3f(AL10.AL_POSITION, x, y, z);
		return new Vector3(x[0], y[0], z[0]);
	}

	public void setListenerVelocity(final Vector3 vel) {
		AL10.alListener3f(AL10.AL_VELOCITY, vel.x, vel.y, vel.z);
	}

	public void setListenerOrientation(final Vector3 at, final Vector3 up) {
		AL10.alListenerfv(AL10.AL_ORIENTATION, new float[] { at.x, at.y, at.z, up.x, up.y, up.z });
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
	public Sound newSound(final FileHandle fileHandle) {
		try (final InputStream in = fileHandle.read()) {
			final OpenAL3DSound sound = new OpenAL3DSound(this);
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
	public Music newMusic(final FileHandle file) {
		return null;
	}

	public int acquireNewBuffer() throws OpenALException {
		final int newBuf = AL10.alGenBuffers();
		OpenAL3DAudio.checkOpenALError();
		this.bufferPool.add(newBuf);
		return newBuf;
	}

	public void disposeBuffer(final int buffer) throws OpenALException {
		this.bufferPool.remove(buffer);
		AL10.alDeleteBuffers(buffer);
		OpenAL3DAudio.checkOpenALError();
	}

	public int acquireNewSource() throws OpenALException {
		final int newSource = AL10.alGenSources();
		OpenAL3DAudio.checkOpenALError();
		this.sourcePool.add(newSource);
		return newSource;
	}

	public void disposeSource(final int source) throws OpenALException {
		this.sourcePool.remove(source);
		AL10.alDeleteSources(source);
		OpenAL3DAudio.checkOpenALError();
	}

	@Override
	public void dispose() {
		ALC10.alcMakeContextCurrent(0);
		ALC10.alcDestroyContext(this.context3D);
		ALC10.alcCloseDevice(this.device);
	}

	@Override
	public void update() {
	}

	public void checkALCError() throws OpenALException {
		final int err = ALC10.alcGetError(this.device);
		if (err != ALC10.ALC_NO_ERROR) {
			throw new RuntimeException(AL10.alGetString(err));
		}
	}

	public static void checkOpenALError() throws OpenALException {
		final int err = AL10.alGetError();
		if (err != AL10.AL_NO_ERROR) {
			throw new OpenALException(err);
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
