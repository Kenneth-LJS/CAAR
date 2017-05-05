package experimental;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat.Type;

import audio.analyzer.AudioAnalyzer;
import audio.analyzer.IAudioAnalyzer;
import audio.analyzer.SoundType;
import utils.ByteUtils;

public class SoundResizing2 {

	   public static void main(String args[]) throws Exception {
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("sample.wav"));
	        byte[] buffer = new byte[4];
	        short[] wav = new short[(int)audioIn.getFrameLength() * 2];
	        int index = 0;
	        while (audioIn.read(buffer) != -1) {
	            wav[index++] = ByteUtils.bytesToShort(buffer, 0, audioIn.getFormat().isBigEndian());
	            wav[index++] = ByteUtils.bytesToShort(buffer, 2, audioIn.getFormat().isBigEndian());
	        }
	        
	        System.out.println("Encoding:\t" + audioIn.getFormat().getEncoding());
	        System.out.println("Sample Rate:\t" + audioIn.getFormat().getSampleRate());
	        System.out.println("Sample Size:\t" + audioIn.getFormat().getSampleSizeInBits());
	        System.out.println("Channels:\t" + audioIn.getFormat().getChannels());
	        System.out.println("Frame Size:\t" + audioIn.getFormat().getFrameSize());
	        System.out.println("Frame Rate:\t" + audioIn.getFormat().getFrameRate());
	        System.out.println("isBigEndian:\t" + audioIn.getFormat().isBigEndian());
	        
	        audioIn.close();

	        float frameRate = audioIn.getFormat().getFrameRate();
	        int sampleSize = (int)Math.round(frameRate * 0.05) * 2; // 0.1 of a second, rounded to nearest 2 samples
	        float[] sampleBuffer = new float[sampleSize];
	        
	        SoundType[] sampleTypes = new SoundType[wav.length / sampleSize];
	        
	        IAudioAnalyzer analyzer = new AudioAnalyzer(sampleSize, 35.0, 175);
			for (int i = 0; i < sampleTypes.length; i++) {
				for (int j = 0; j < sampleBuffer.length; j++) {
					sampleBuffer[j] = wav[i * sampleSize + j];
				}
				sampleTypes[i] = analyzer.analyze(sampleBuffer);
			}
	        
			final float vowelSpeedup = 0.7f; // multiplies vowel duration by this factor
			final float consonantSpeedup = 1f; // multiplies consonant duration by this factor
			
	        List<Short> trimmedSound = new ArrayList<Short>();
	        index = 0;
	        while (index < sampleTypes.length) {
	        	if (sampleTypes[index] == SoundType.SILENCE) {
	        		int silenceStartIndex = index;
	        		while (index < sampleTypes.length && sampleTypes[index] == SoundType.SILENCE) {
	        			index++;
	        		}
	        		if (index == sampleTypes.length) {
	            		for (int j = 0; j < sampleSize; j++) {
	            			trimmedSound.add(wav[silenceStartIndex * sampleSize + j]);
	            		}
	        		} else {
	        			int wavStart = silenceStartIndex * sampleSize;
	        			int wavEnd = index * sampleSize;
	        			short[] sampleMix = shortenSilence(wav, wavStart, wavEnd, 3 * sampleSize);
	        			for (int j = 0; j < sampleSize; j++) {
	        				trimmedSound.add(sampleMix[j]);
	        			}
	        		}
	        	} else {
	        		System.out.println(sampleTypes[index].toString());
					for (int j = 0; j < sampleBuffer.length; j++) {
						sampleBuffer[j] = wav[index * sampleSize + j];
					}
	        		if (sampleTypes[index] == SoundType.VOWEL) { // SPEEDUP HERE
	        			//sampleBuffer = interpolate(sampleBuffer, vowelSpeedup);
	        		} else {
	        			//sampleBuffer = interpolate(sampleBuffer, consonantSpeedup);
	        		}
	        		for (int j = 0; j < sampleBuffer.length; j++) {
	        			trimmedSound.add((short)sampleBuffer[j]);
	        		}
	        		
//	        		// leave sounds as default
//	        		for (int j = 0; j < sampleSize; j++) {
//	        			trimmedSound.add(wav[index * sampleSize + j]);
//	        		}
	        		index++;
	        	}
	        }
	        for (int i = sampleTypes.length * sampleSize; i < wav.length; i++) {
	        	trimmedSound.add(wav[i]);
	        }
	        
//	        List<Short> trimmedSound = new ArrayList<Short>();
//	        index = 0;
//	        boolean prevSilence = false;
//	        while (index < wav.length) {
//	            System.out.println("Frame " + index + " / " + wav.length);
//	            if (index + sampleSize >= wav.length) {
//	                for (; index < wav.length; index++) {
//	                    trimmedSound.add(wav[index]);
//	                }
//	                continue;
//	            }
//	            if (isSilence(wav, index, sampleSize)) {
//	                if (!prevSilence) {
//	                    for (int i = 0; i < sampleSize / 2; i++) {
//	                        trimmedSound.add(wav[index + i]);
//	                    }
//	                    prevSilence = true;
//	                }
//	            } else {
//	                prevSilence = false;
//	                for (int i = 0; i < sampleSize; i++) {
//	                    trimmedSound.add(wav[index + i]);
//	                }
//	            }
//	            index += sampleSize;
//	        }
	        
	        byte[] sound = new byte[trimmedSound.size() * 2];
	        index = 0;
	        for (short amp : trimmedSound) {
	            byte[] buf = ByteUtils.shortToBytes(amp, audioIn.getFormat().isBigEndian());
	            sound[index++] = buf[0];
	            sound[index++] = buf[1];
	        }
	        AudioInputStream stream = new AudioInputStream(new ByteArrayInputStream(sound), audioIn.getFormat(), (long) sound.length / 4);
	        File file = new File("file.wav");
	        AudioSystem.write(stream, Type.WAVE, file);
//	        List<Double> sampleVolumes = new ArrayList<Double>();

//	        System.out.println(sampleVolumes.size());
//	        PrintWriter writer = new PrintWriter("out.txt", "UTF-8");
//	        for (double d : sampleVolumes) {
//	            writer.println(d + "\t1");
//	        }
//	        writer.close();
	        System.out.println("Done.");
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
	   
		private static float[] interpolate(float[] buff, float comp) {
			float[] buff2 = new float[buff.length];
			int newTotal = (int) ((float) buff.length / comp);
			for (int i = 0; i < newTotal; i++) {
				float index = comp * i;
				float b1 = buff[(int) Math.floor(index)];
				float b2 = buff[Math.min((int) Math.ceil(index), buff.length - 1)];
				float rem = (index + 0.0000001f) % 1;
				float newVal = (b1 + (b2 - b1) * rem);
				buff2[i] = newVal;
			}

			return buff2;
		}
	    
}
