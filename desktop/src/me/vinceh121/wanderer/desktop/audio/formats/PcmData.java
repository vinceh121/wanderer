package me.vinceh121.wanderer.desktop.audio.formats;

public class PcmData {
	private final byte[] data;
	private final int format, sampleRate;

	public PcmData(final byte[] data, final int format, final int sampleRate) {
		this.data = data;
		this.format = format;
		this.sampleRate = sampleRate;
	}

	public byte[] getData() {
		return this.data;
	}

	public int getFormat() {
		return this.format;
	}

	public int getSampleRate() {
		return this.sampleRate;
	}

	@Override
	public String toString() {
		return "PcmData [data=" + this.data.length + ", format=" + this.format + ", sampleRate=" + this.sampleRate
				+ "]";
	}
}
