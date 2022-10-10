package me.vinceh121.wanderer.desktop.audio.formats;

import java.io.IOException;
import java.io.InputStream;

public interface AudioLoader {
	PcmData readPCM(InputStream in) throws IOException;
}
