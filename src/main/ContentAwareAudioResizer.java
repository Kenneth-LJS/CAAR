package main;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.ArrayUtils;
import org.jtransforms.fft.FloatFFT_1D;

import audio.Audio;
import gui.SetupForm;
import utils.ByteUtils;
import utils.GraphUtils;

public class ContentAwareAudioResizer {

    public static void main(String args[]) {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    SetupForm frame = new SetupForm();
//                    // todo: add listeners
//                    frame.setVisible(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        String[] files = new String[] { "ah", "eh", "ch" };
        for (String file : files) {
            try {
            	Audio audioIn = new Audio(file + ".wav");
                int totalSamples = audioIn.available() / 2;
                Float[] fsamples = new Float[totalSamples];
                audioIn.read(fsamples);
                float[] samples = ArrayUtils.toPrimitive(fsamples);
                
                //GraphUtils.dataToGraph(samples, "test1.bmp");
                FloatFFT_1D fft = new FloatFFT_1D(samples.length);
                fft.realForward(samples);
                //GraphUtils.dataToGraph(samples, "test2.bmp");
                samples = filterRealComponent(samples);
                for (int j = 0; j < samples.length; j++) {
                    samples[j] = Math.abs(samples[j]);
                }
                GraphUtils.dataToGraph(samples, "test3 - " + file + ".bmp");
                System.out.println(file + " : " + calcFormula(samples));
//                System.out.println("Counted samples: " + count);
//                System.out.println("Total samples: " + totalSamples);
                audioIn.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        System.out.println("Done.");
    }
    
    public static float calcFormula(float[] in) {
        // variance
        double sum = 0;
        for (int i = 0; i < in.length; i++) {
            sum += in[i];
        }
        float mean = (float)(sum / in.length);
        sum = 0;
        for (int i = 0; i < in.length; i++) {
            sum += Math.pow(mean - in[i], 2);
        }
        return (float)(sum / in.length);
    }
    
    public static float[] filterRealComponent(float[] in) {
        boolean even = in.length % 2 == 0;
        float[] out = new float[1 + in.length / 2];
        int beginOffset = 0;
        if (even) {
            out[0] = in[1];
            beginOffset = 1;
        }
        for (int i = 0; i < in.length; i += 2) {
            out[beginOffset + i / 2] = in[i];
        }
        return out;
    }
    
}
