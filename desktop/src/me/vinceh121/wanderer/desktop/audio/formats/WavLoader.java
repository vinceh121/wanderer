package me.vinceh121.wanderer.desktop.audio.formats;

import java.io.IOException;
import java.io.InputStream;

import org.lwjgl.openal.AL10;

public class WavLoader implements AudioLoader {

	@Override
	public PcmData readPCM(final InputStream in) throws IOException {
		try (WavInputStream w = new WavInputStream(in)) {
			final byte[] data = w.readAllBytes();
			final int format = w.getChannels() == 2 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16;
			final int sampleRate = w.getSampleRate();
			return new PcmData(data, format, sampleRate);
		}
	}
}
