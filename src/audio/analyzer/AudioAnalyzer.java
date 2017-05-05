package audio.analyzer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import org.jtransforms.fft.FloatFFT_1D;

import utils.ByteUtils;
import utils.GraphUtils;

// optimized for speed than readability
public class AudioAnalyzer implements IAudioAnalyzer {

	private FloatFFT_1D fft;
	private double silenceThreshold;
	private int consonantThreshold;
	
	private float[] buffer;
	
	public AudioAnalyzer(int sampleSize, double silenceThresholdDb, int consonantThreshold) {
		
		this.fft = new FloatFFT_1D(sampleSize);
		
		// silenceThresholdDb: dB below which is considered silence
		// convert from dB into energy threshold (linear for faster computations)
		// Math.pow(silenceThresholdDb / 20, 10)
		// square it so we don't have to sqrt later in silence threshold,
		// so we pow by 20 instead of 10
		this.silenceThreshold = (float)Math.pow(silenceThresholdDb / 20, 20);
		
		this.consonantThreshold = consonantThreshold;
		
		this.buffer = new float[sampleSize / 2];
	}
	
	public SoundType analyze(float[] sample) {
		if (isSilence(sample, this.silenceThreshold)) {
			return SoundType.SILENCE;
		} else if (isConsonant(sample, this.consonantThreshold)){
			return SoundType.CONSONANT;
		} else {
			return SoundType.VOWEL;
		}
	}

	private boolean isSilence(float[] sample, double silenceThreshold) {
		// calculates the mean square of sample
		double sampleEnergy = 0;
		for (int i = 0; i < sample.length; i++) {
			sampleEnergy += sample[i] * sample[i];
		}
		sampleEnergy /= sample.length;
		return sampleEnergy < silenceThreshold;
	}
	
	int x = 0;
	Scanner sc = new Scanner(System.in);
	
//	private boolean isConsonant(float[] sample, int consonantThreshold) {
//		
//        byte[] sound = new byte[sample.length * 2];
//        for (int i = 0; i < sample.length; i++) {
//        	byte[] buf = ByteUtils.shortToBytes((short)sample[i], true);
//        	sound[2 * i] = buf[0];
//        	sound[2 * i + 1] = buf[1];
//        }
//		
//		// fourier transform sample
//        this.fft.realForward(sample);
//        // filter real portion, and get absolute value
//        for (int i = 0; i < buffer.length; i++) {
//        	buffer[i] = Math.abs(sample[i << 1]);
//        }
//
//        float[] copyBuffer = new float[buffer.length];
//        System.arraycopy(buffer, 0, copyBuffer, 0, buffer.length);
//        
//        int triangles = 0;
//        while (findTriangle(buffer)) {
//        	triangles++;
//        }
//        
//        AudioInputStream stream = new AudioInputStream(new ByteArrayInputStream(sound), new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true), (long) sound.length / 2);
//        Clip clip = null;
//        try {
//        	clip = AudioSystem.getClip();
//			clip.open(stream);
//			clip.loop(Integer.MAX_VALUE);
//			clip.start();
//		} catch (LineUnavailableException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//        
//        String type = "";
//        while (!type.equals("v") && !type.equals("c")) {
//        	type = sc.next();
//        }
//        clip.stop();
//        try {
//			GraphUtils.dataToGraph(copyBuffer, String.format("%03d %s-%d.png", x++, type, triangles));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        return triangles > consonantThreshold;
//	}
	
	private boolean isConsonant(float[] sample, int consonantThreshold) {
		// fourier transform sample
        this.fft.realForward(sample);
        // filter real portion, and get absolute value
        for (int i = 0; i < buffer.length; i++) {
        	buffer[i] = Math.abs(sample[i << 1]);
        }
        // formula
        int triangles = 0;
        while (findTriangle(buffer)) {
        	triangles++;
        	if (triangles > consonantThreshold) {
        		return true;
        	}
        }
        return false;
	}
	
    public static boolean findTriangle(float[] arr) {
        int maxIndex = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIndex]) {
                maxIndex = i;
            }
        }
        if (arr[maxIndex] == 0) {
            return false;
        }
        int curWidth = 3; // tweak
        int peakIndex = maxIndex;
        float peakVal = arr[maxIndex];
        float totalWidthCount = 0;
        int pointCount = 0;
        for (int i = Math.max(maxIndex - curWidth, 0); i <= Math.min(peakIndex + curWidth, arr.length - 1); i++) {
            if (i == peakIndex) {
                continue;
            }
            totalWidthCount += peakVal * Math.abs(i - peakIndex) / (peakVal - arr[i]); 
            pointCount++;
        }
        while (curWidth < totalWidthCount / pointCount) {
            curWidth++;
            if (peakIndex - curWidth >= 0) {
                totalWidthCount += peakVal * curWidth / (peakVal - arr[peakIndex - curWidth]); 
                pointCount++;
            }
            if (peakIndex + curWidth < arr.length) {
                totalWidthCount += peakVal * curWidth / (peakVal - arr[peakIndex + curWidth]); 
                pointCount++;
            }
        }
        for (int i = Math.max(peakIndex - curWidth, 0); i <= Math.min(peakIndex + curWidth, arr.length - 1); i++) {
            arr[i] = 0;
        }
        return true;
    }
	
}
