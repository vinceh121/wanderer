package me.vinceh121.wanderer.desktop.audio.formats;

import java.io.IOException;
import java.io.InputStream;

import org.lwjgl.openal.AL10;

import com.badlogic.gdx.backends.lwjgl3.audio.OggInputStream;

public class OggLoader implements AudioLoader {
	@Override
	public PcmData readPCM(final InputStream in) throws IOException {
		try (OggInputStream ogg = new OggInputStream(in)) {
			final byte[] data = ogg.readAllBytes();
			final int format = ogg.getChannels() == 2 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16;
			final int sampleRate = ogg.getSampleRate();
			return new PcmData(data, format, sampleRate);
		}
	}
}
