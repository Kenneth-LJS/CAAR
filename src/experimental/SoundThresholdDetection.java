package experimental;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import utils.ByteUtils;

public class SoundThresholdDetection {

    public static void main(String args[]) throws Exception {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("sample.wav"));
        float frameRate = audioIn.getFormat().getFrameRate();
        int sampleSize = (int)Math.round(frameRate * 0.05) * 2; // 0.1 of a second, rounded to nearest 2 samples
        byte[] buffer = new byte[sampleSize * 2];
        short[] sample = new short[sampleSize];
        List<Double> sampleVolumes = new ArrayList<Double>();
        while (audioIn.read(buffer) >= buffer.length) {
            double totalSample = 0;
            for (int i = 0; i < sample.length; i++) {
                sample[i] = ByteUtils.bytesToShort(buffer, i * 2, audioIn.getFormat().isBigEndian());
//                totalSample = Math.max(Math.abs(sample[i]), totalSample);
                if (sample[i] > 500) {
                    totalSample++;
                }
            }
            sampleVolumes.add(totalSample);
        }
        audioIn.close();
        System.out.println(sampleVolumes.size());
        PrintWriter writer = new PrintWriter("out.txt", "UTF-8");
        for (double d : sampleVolumes) {
            writer.println(d + "\t1");
        }
        writer.close();
    }
    
}
