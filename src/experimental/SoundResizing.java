package experimental;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat.Type;

import audio.analyzer.AudioAnalyzer;
import audio.analyzer.IAudioAnalyzer;
import audio.analyzer.SoundType;
import utils.ByteUtils;

public class SoundResizing {

	public static void main(String args[]) throws IOException, UnsupportedAudioFileException {
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("sample.wav"));

		float frameRate = audioIn.getFormat().getFrameRate();
		final int sampleSize = (int)Math.round(frameRate * 0.05) * 2; // 0.1 seconds per sample, rounded to nearest even number


		int totalSamples = audioIn.available() / 2;

		IAudioAnalyzer analyzer = new AudioAnalyzer(sampleSize, -20.0, 100);
		final SoundType[] sampleTypes = new SoundType[totalSamples / sampleSize];
		final float[] sampleBuffer = new float[sampleSize];
		final byte[] byteBuffer = new byte[4];

		for (int i = 0; i < sampleTypes.length; i++) {
			for (int j = 0; j < sampleBuffer.length; j += 2) {
				audioIn.read(byteBuffer);
				sampleBuffer[j] = (float)ByteUtils.bytesToShort(byteBuffer, 0, audioIn.getFormat().isBigEndian());
				sampleBuffer[j + 1] = (float)ByteUtils.bytesToShort(byteBuffer, 2, audioIn.getFormat().isBigEndian());
			}
			sampleTypes[i] = analyzer.analyze(sampleBuffer);
		}
		audioIn.close();

		final float silenceTime = 0.3f; // shortens silence to this length
		final float vowelSpeedup = 0.3f; // multiplies vowel duration by this factor
		final float consonantSpeedup = 1f; // multiplies consonant duration by this factor
		
		final int silenceSamples = (int)Math.round(silenceTime * frameRate);
		final int silenceLargeSamples = (int)Math.ceil((double)silenceSamples / sampleSize);
		
		int index = 0;
		long outSamples = 0;
		//          List<Entry<SoundType, Integer>> sampleLengths = mergeSampleTypes(sampleTypes, sampleSize);
		//          for (Entry<SoundType, Integer> e : sampleLengths) {
		//        	  if (e.getKey() == SoundType.SILENCE) {
		//        		  outSamples += Math.min(e.getValue(), silenceSamples);
		//        	  } else if (e.getKey() == SoundType.VOWEL) {
		//        		  outSamples += (int)Math.round(vowelSpeedup * e.getValue());
		//        	  } else if (e.getKey() == SoundType.CONSONANT) {
		//        		  outSamples += (int)Math.round(consonantSpeedup * e.getValue());
		//        	  }
		//          }
		while (index < sampleTypes.length) {
			if (sampleTypes[index] == SoundType.VOWEL) {
				outSamples += (int)Math.round(vowelSpeedup * sampleSize);
				index++;
			} else if (sampleTypes[index] == SoundType.CONSONANT) {
				outSamples += (int)Math.round(consonantSpeedup * sampleSize);
				index++;
			} else if (sampleTypes[index] == SoundType.SILENCE){
				int nextIndex = index + 1;
				for (nextIndex = index + 1; nextIndex < sampleTypes.length; nextIndex++) {
					if (sampleTypes[nextIndex] != SoundType.SILENCE) {
						break;
					}
				}
				int sampleCount = (nextIndex - index) * sampleSize;
				outSamples += Math.min(sampleCount, silenceSamples);
				index = nextIndex;
			} else {
				// soundtype not supported
				index++;
			}
		}
		outSamples += totalSamples % sampleSize; // just include the last sample

		final AudioInputStream secondPassAudio = AudioSystem.getAudioInputStream(new File("sample.wav"));

		InputStream stream = new InputStream() {
			
			private byte[] buffer = null;
			private int bufferIndex;
			private int sampleIndex = 0;
			
			@Override
			public int read() throws IOException {
				if (buffer == null || bufferIndex >= buffer.length) {
					if (!loadNextBuffer()) {
						return -1;
					}
				}
				byte val = buffer[bufferIndex];
				bufferIndex++;
				return Byte.toUnsignedInt(val);
			}
			
			public int read(byte[] b, int off, int len) throws IOException {
//				int bytesRead = 0;
//				int lengthLeft = len;
//				int bufferLeft = buffer.length - bufferIndex;
//				while (true) {
//					if (lengthLeft <= 0) {
//						return bytesRead;
//					} else if (bufferLeft <= 0) {
//						if (!loadNextBuffer()) {
//							if (bytesRead == 0) {
//								return -1;
//							} else {
//								return bytesRead;
//							}
//						} else {
//							bufferLeft = buffer.length;
//						}
//					}
//					if (lengthLeft > bufferLeft) {
//						
//					}
//				}
				int bytesRead = 0;
				int val = read();
				if (val == -1) {
					return -1;
				}
				b[off] = (byte)val;
				for (int i = 1; i < len; i++) {
					val = read();
					if (val != -1) {
						b[off + i] = (byte)val;
						bytesRead++;
					} else {
						break;
					}
				}
				return bytesRead;
			}
			
			public int read(byte[] b) throws IOException {
				return read(b, 0, b.length);
			}
			
			private boolean loadNextBuffer() throws IOException {
				System.out.println("Next buffer");
				if (sampleIndex >= sampleTypes.length) {
					return false;
				}
				bufferIndex = 0;
				SoundType type = sampleTypes[sampleIndex];
				if (type == SoundType.VOWEL || type == SoundType.CONSONANT) {
					for (int j = 0; j < sampleBuffer.length; j += 2) {
						secondPassAudio.read(byteBuffer);
						sampleBuffer[j] = (float)ByteUtils.bytesToShort(byteBuffer, 0, secondPassAudio.getFormat().isBigEndian());
						sampleBuffer[j + 1] = (float)ByteUtils.bytesToShort(byteBuffer, 2, secondPassAudio.getFormat().isBigEndian());
					}
					// TODO shorten buffer here
					float[] buffer1 = sampleBuffer;
					short[] buffer2 = new short[buffer1.length];
					for (int i = 0; i < buffer2.length; i++) {
						buffer2[i] = (short)Math.round(buffer1[i]);
					}
					buffer = toByteArray(buffer2, secondPassAudio.getFormat().isBigEndian());
					sampleIndex++;
				} else if (type == SoundType.SILENCE) {
					int nextIndex = sampleIndex + 1;
					for (nextIndex = sampleIndex + 1; nextIndex < sampleTypes.length; nextIndex++) {
						if (sampleTypes[nextIndex] != SoundType.SILENCE) {
							break;
						}
					}
					int largeSampleCount = nextIndex - sampleIndex;
					short[] tempBuffer;
					if (largeSampleCount > 2 * silenceLargeSamples) {
						// only read small portion
						tempBuffer = new short[2 * silenceLargeSamples * sampleSize];
						int midpoint = silenceLargeSamples * sampleSize;
						for (int j = 0; j < midpoint; j += 2) {
							secondPassAudio.read(byteBuffer);
							tempBuffer[j] = ByteUtils.bytesToShort(byteBuffer, 0, secondPassAudio.getFormat().isBigEndian());
							tempBuffer[j + 1] = ByteUtils.bytesToShort(byteBuffer, 2, secondPassAudio.getFormat().isBigEndian());
						}
						int blankSamples = largeSampleCount - 2 * silenceLargeSamples;
						for (int i = 0; i < blankSamples; i++) {
							for (int j = 0; j < midpoint; j += 2) {
								secondPassAudio.read(byteBuffer);
							}
						}
						for (int j = midpoint; j < tempBuffer.length; j += 2) {
							secondPassAudio.read(byteBuffer);
							tempBuffer[j] = ByteUtils.bytesToShort(byteBuffer, 0, secondPassAudio.getFormat().isBigEndian());
							tempBuffer[j + 1] = ByteUtils.bytesToShort(byteBuffer, 2, secondPassAudio.getFormat().isBigEndian());
						}
					} else {
						tempBuffer = new short[largeSampleCount * sampleSize];
						for (int j = 0; j < tempBuffer.length; j += 2) {
							secondPassAudio.read(byteBuffer);
							tempBuffer[j] = ByteUtils.bytesToShort(byteBuffer, 0, secondPassAudio.getFormat().isBigEndian());
							tempBuffer[j + 1] = ByteUtils.bytesToShort(byteBuffer, 2, secondPassAudio.getFormat().isBigEndian());
						}
					}
					tempBuffer = shortenSilence(tempBuffer, 0, tempBuffer.length, silenceSamples);
					buffer = toByteArray(tempBuffer, secondPassAudio.getFormat().isBigEndian());
					sampleIndex = nextIndex;
				}
				System.out.println((sampleIndex + 1) + " / " + sampleTypes.length);
				return true;
			}
			
		};

		AudioInputStream outStream = new AudioInputStream(stream, audioIn.getFormat(), outSamples);
		File file = new File("file.wav");
		AudioSystem.write(outStream, Type.WAVE, file);
		System.out.println("Done.");

	}

	private static List<Entry<SoundType, Integer>> mergeSampleTypes(SoundType[] soundTypes, int sampleSize) {
		// sampleSize = number of values per sample
		// soundTypes = type of sound per sample
		List<Entry<SoundType, Integer>> result = new ArrayList<Entry<SoundType, Integer>>();

		int index = 0;
		while (index < soundTypes.length) {
			int nextIndex = index + 1;
			for (nextIndex = index + 1; nextIndex < soundTypes.length; nextIndex++) {
				if (soundTypes[nextIndex] != soundTypes[index]) {
					break;
				}
			}
			int length = (nextIndex - index) * sampleSize;
			result.add(new SimpleEntry<SoundType, Integer>(soundTypes[index], length));
			index = nextIndex;
		}
		return result;
	}
	
    private static short[] shortenSilence(short[] wav,  int silenceStart, int silenceEnd, int newLength) {
    	// silence starts at 'silenceStart' inclusive, ends at 'silenceEnd' exclusive
    	if (silenceEnd - silenceStart <= newLength) {
    		short[] out = new short[silenceEnd - silenceStart];
    		for (int i = 0; i < out.length; i++) {
    			out[i] = wav[silenceStart + i];
    		}
    		return out;
    	}
    	short[] out = new short[newLength];
    	for (int i = 0; i < out.length; i++) {
    		float val = (float)i / (out.length - 1);
    		out[i] = (short)Math.round(wav[silenceStart + i] * val + wav[silenceEnd - out.length + i] * (1 - val));
    	}
    	return out;
    }

    private static byte[] toByteArray(short[] samples, boolean isBigEndian) {
    	byte[] buffer = new byte[samples.length * 2];
		for (int i = 0; i < samples.length; i++) {
			byte[] buf = ByteUtils.shortToBytes(samples[i], isBigEndian);
			buffer[i * 2] = buf[0];
			buffer[i * 2 + 1] = buf[1];
		}
		return buffer;
    }
    
}
