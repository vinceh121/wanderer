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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.EnumerateAllExt;
import org.lwjgl.openal.SOFTReopenDevice;

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
	private final long device, context3D, contextGeneral;
	private final Set<Integer> bufferPool = new HashSet<>();
	private final Set<OpenAL3DSource> sourcePool = new HashSet<>();

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

		this.contextGeneral = ALC10.alcCreateContext(this.device, (IntBuffer) null);
		if (this.contextGeneral == 0) {
			throw new IllegalStateException("alcCreateContext failed");
		}

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
		this.set3DContext();
		AL10.alListener3f(AL10.AL_POSITION, pos.x, pos.y, pos.z);
	}

	@Override
	public Vector3 getListenerPosition() {
		final float[] x = new float[1];
		final float[] y = new float[1];
		final float[] z = new float[1];
		this.set3DContext();
		AL10.alGetListener3f(AL10.AL_POSITION, x, y, z);
		return new Vector3(x[0], y[0], z[0]);
	}

	@Override
	public void setListenerVelocity(final Vector3 vel) {
		this.set3DContext();
		AL10.alListener3f(AL10.AL_VELOCITY, vel.x, vel.y, vel.z);
	}

	@Override
	public void setListenerOrientation(final Vector3 at, final Vector3 up) {
		this.set3DContext();
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
		final int newBuf = AL10.alGenBuffers();
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
		AL10.alDeleteBuffers(buffer);
		OpenAL3DAudio.checkOpenALError();
	}

	public void registerSource(final OpenAL3DSource source) {
		this.sourcePool.add(source);
	}

	public void unregisterSource(final SoundEmitter3D source) {
		this.sourcePool.remove(source);
	}

	public void set3DContext() {
		ALC10.alcMakeContextCurrent(this.context3D);
	}

	public void setGeneralContext() {
		ALC10.alcMakeContextCurrent(this.contextGeneral);
	}

	public long getContext3D() {
		return this.context3D;
	}

	public long getContextGeneral() {
		return this.contextGeneral;
	}

	@Override
	public void update() {
		final Set<OpenAL3DSource> toRemove = new HashSet<>();

		for (final OpenAL3DSource src : this.sourcePool) {
			if (src.isDisposed()) {
				toRemove.add(src);
				continue;
			}

			src.update();

			try {
				OpenAL3DAudio.checkOpenALError();
			} catch (final OpenALException e) {
				throw new RuntimeException(e);
			}
		}

		this.sourcePool.removeAll(toRemove);
	}

	public void checkALCError() throws OpenALException {
		final int err = ALC10.alcGetError(this.device);
		if (err != ALC10.ALC_NO_ERROR) {
			throw new RuntimeException(AL10.alGetString(err));
		}
	}

	@Override
	public void dispose() {
		for (final OpenAL3DSource src : this.sourcePool) {
			src.dispose();
		}
		ALC10.alcMakeContextCurrent(0);
		ALC10.alcDestroyContext(this.context3D);
		ALC10.alcCloseDevice(this.device);
	}

	public static void checkOpenALError() throws OpenALException {
		final int err = AL10.alGetError();
		if (err != AL10.AL_NO_ERROR) {
			throw new OpenALException(err);
		}
	}

	public static void runtimeCheckOpenAlError() {
		try {
			OpenAL3DAudio.checkOpenALError();
		} catch (final OpenALException e) {
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

	@Override
	public boolean switchOutputDevice(String deviceIdentifier) {
		return SOFTReopenDevice.alcReopenDeviceSOFT(this.device, deviceIdentifier, (IntBuffer) null);
	}

	@Override
	public String[] getAvailableOutputDevices() {
		final List<String> devices = ALUtil.getStringList(0, EnumerateAllExt.ALC_ALL_DEVICES_SPECIFIER);

		if (devices == null) {
			return new String[0];
		}

		return devices.toArray(l -> new String[l]);
	}

	static {
		OpenAL3DAudio.AUDIO_LOADERS.put("ogg", new OggLoader());
		OpenAL3DAudio.AUDIO_LOADERS.put("wav", new WavLoader());
	}
}
