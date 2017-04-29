package experimental;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import utils.ByteUtils;

import javax.sound.sampled.AudioFileFormat.Type;

public class SoundGenerator {

	private static final float SAMPLE_RATE = 44100;
	private static TargetDataLine targetLine;

	public static void main(String[] args) throws IOException {
		AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
		SineSound s = new SineSound(format, 44100 * 3, Math.PI / 100);
		AudioSystem.write(s.getStream(), Type.WAVE, new File("file.wav"));

		System.out.println("Done.");
	}

	private static class SineByteStream extends InputStream {

		private long lengthLeft;
		private double theta = 0;
		private double stepSize = Math.PI / 100;

		public SineByteStream(long length) {
			this.lengthLeft = length + (16 - (length % 16));
		}

		@Override
		public int read(byte[] b) {
			return read(b, 0, b.length);
		}

		@Override
		public int read(byte[] b, int off, int len) {
			if (lengthLeft == 0) {
				return -1;
			}
			len = b.length - b.length % 2;
			for (int i = 0; i < len; i += 2) {
				byte[] arr = ByteUtils.shortToBytes((short) Math.floor(Math.sin(theta) * 3000), true);
				b[off + i] = arr[0];
				b[off + i + 1] = arr[1];
				theta = (theta + stepSize) % (2 * Math.PI);
				lengthLeft -= 16;
			}
			return len;
		}

		@Override
		public int read() throws IOException {
			throw new IOException("cannot read a single byte if frame size > 1");
		}
	}

}
