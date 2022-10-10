/*******************************************************************************
 * From LibGDX, copied and adapted to be usable for Wanderer
 *
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package me.vinceh121.wanderer.desktop.audio.formats;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;

/** @author Nathan Sweet */
public class WavInputStream extends FilterInputStream {
	private int channels, sampleRate, dataRemaining;

	public WavInputStream(final InputStream in) {
		super(in);
		try {
			if (this.read() != 'R' || this.read() != 'I' || this.read() != 'F' || this.read() != 'F') {
				throw new GdxRuntimeException("RIFF header not found");
			}

			this.skipFully(4);

			if (this.read() != 'W' || this.read() != 'A' || this.read() != 'V' || this.read() != 'E') {
				throw new GdxRuntimeException("Invalid wave file header");
			}

			final int fmtChunkLength = this.seekToChunk('f', 'm', 't', ' ');

			final int type = this.read() & 0xff | (this.read() & 0xff) << 8;
			if (type != 1) {
				throw new GdxRuntimeException("WAV files must be PCM: " + type);
			}

			this.channels = this.read() & 0xff | (this.read() & 0xff) << 8;
			if (this.channels != 1 && this.channels != 2) {
				throw new GdxRuntimeException("WAV files must have 1 or 2 channels: " + this.channels);
			}

			this.sampleRate = this.read() & 0xff | (this.read() & 0xff) << 8 | (this.read() & 0xff) << 16
					| (this.read() & 0xff) << 24;

			this.skipFully(6);

			final int bitsPerSample = this.read() & 0xff | (this.read() & 0xff) << 8;
			if (bitsPerSample != 16) {
				throw new GdxRuntimeException("WAV files must have 16 bits per sample: " + bitsPerSample);
			}

			this.skipFully(fmtChunkLength - 16);

			this.dataRemaining = this.seekToChunk('d', 'a', 't', 'a');
		} catch (final Throwable ex) {
			StreamUtils.closeQuietly(this);
			throw new GdxRuntimeException("Error reading WAV file", ex);
		}
	}

	private int seekToChunk(final char c1, final char c2, final char c3, final char c4) throws IOException {
		while (true) {
			boolean found = this.read() == c1;
			found &= this.read() == c2;
			found &= this.read() == c3;
			found &= this.read() == c4;
			final int chunkLength = this.read() & 0xff | (this.read() & 0xff) << 8 | (this.read() & 0xff) << 16
					| (this.read() & 0xff) << 24;
			if (chunkLength == -1) {
				throw new IOException("Chunk not found: " + c1 + c2 + c3 + c4);
			}
			if (found) {
				return chunkLength;
			}
			this.skipFully(chunkLength);
		}
	}

	private void skipFully(int count) throws IOException {
		while (count > 0) {
			final long skipped = this.in.skip(count);
			if (skipped <= 0) {
				throw new EOFException("Unable to skip.");
			}
			count -= skipped;
		}
	}

	@Override
	public int read(final byte[] buffer) throws IOException {
		if (this.dataRemaining == 0) {
			return -1;
		}
		int offset = 0;
		do {
			final int length = Math.min(super.read(buffer, offset, buffer.length - offset), this.dataRemaining);
			if (length == -1) {
				if (offset > 0) {
					return offset;
				}
				return -1;
			}
			offset += length;
			this.dataRemaining -= length;
		} while (offset < buffer.length);
		return offset;
	}

	public int getChannels() {
		return this.channels;
	}

	public int getSampleRate() {
		return this.sampleRate;
	}

	public int getDataRemaining() {
		return this.dataRemaining;
	}
}