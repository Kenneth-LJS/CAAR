package experimental;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import audio.SoundType;

import javax.sound.sampled.AudioFileFormat.Type;
import utils.ByteUtils;

public class SilenceRemover {

    public static void main(String args[]) throws Exception {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("sample.wav"));
        byte[] buffer = new byte[4];
        short[] wav = new short[(int)audioIn.getFrameLength() * 2];
        int index = 0;
        while (audioIn.read(buffer) != -1) {
            wav[index++] = ByteUtils.bytesToShort(buffer, 0, audioIn.getFormat().isBigEndian());
            wav[index++] = ByteUtils.bytesToShort(buffer, 2, audioIn.getFormat().isBigEndian());
        }
        audioIn.close();

        float frameRate = audioIn.getFormat().getFrameRate();
        int sampleSize = (int)Math.round(frameRate * 0.05) * 2; // 0.1 of a second, rounded to nearest 2 samples
        
        SoundType[] sampleTypes = new SoundType[wav.length / sampleSize];
        
        for (int i = 0; i < sampleTypes.length; i++) {
        	System.out.println("Frame " + (i + 1) + " / " + sampleTypes.length);
        	for (int j = 0; j < sampleSize; j++) {
        		if (isSilence(wav, i * sampleSize, sampleSize)) {
        			sampleTypes[i] = SoundType.NOISE;
        		} else {
        			sampleTypes[i] = SoundType.CONSONANT;
        		}
        	}
        }
        
        List<Short> trimmedSound = new ArrayList<Short>();
        index = 0;
        while (index < sampleTypes.length) {
        	if (sampleTypes[index] == SoundType.NOISE) {
        		int silenceStartIndex = index;
        		while (index < sampleTypes.length && sampleTypes[index] == SoundType.NOISE) {
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
        		for (int j = 0; j < sampleSize; j++) {
        			trimmedSound.add(wav[index * sampleSize + j]);
        		}
        		index++;
        	}
        }
        for (int i = sampleTypes.length * sampleSize; i < wav.length; i++) {
        	trimmedSound.add(wav[i]);
        }
        
//        List<Short> trimmedSound = new ArrayList<Short>();
//        index = 0;
//        boolean prevSilence = false;
//        while (index < wav.length) {
//            System.out.println("Frame " + index + " / " + wav.length);
//            if (index + sampleSize >= wav.length) {
//                for (; index < wav.length; index++) {
//                    trimmedSound.add(wav[index]);
//                }
//                continue;
//            }
//            if (isSilence(wav, index, sampleSize)) {
//                if (!prevSilence) {
//                    for (int i = 0; i < sampleSize / 2; i++) {
//                        trimmedSound.add(wav[index + i]);
//                    }
//                    prevSilence = true;
//                }
//            } else {
//                prevSilence = false;
//                for (int i = 0; i < sampleSize; i++) {
//                    trimmedSound.add(wav[index + i]);
//                }
//            }
//            index += sampleSize;
//        }
        
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
//        List<Double> sampleVolumes = new ArrayList<Double>();

//        System.out.println(sampleVolumes.size());
//        PrintWriter writer = new PrintWriter("out.txt", "UTF-8");
//        for (double d : sampleVolumes) {
//            writer.println(d + "\t1");
//        }
//        writer.close();
        System.out.println("Done.");
    }
    
    private static boolean isSilence(short[] sample, int offset, int sampleSize) {
        int count = 0;
        for (int i = 0; i < sampleSize; i++) {
            if (sample[offset + i] > 500) {
                count++;
            }
        }
        return count < 100;
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
    
}
