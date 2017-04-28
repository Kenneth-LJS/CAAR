package experimental;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
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
        
        List<Short> trimmedSound = new ArrayList<Short>();
        index = 0;
        boolean prevSilence = false;
        while (index < wav.length) {
            System.out.println("Frame " + index + " / " + wav.length);
            if (index + sampleSize >= wav.length) {
                for (; index < wav.length; index++) {
                    trimmedSound.add(wav[index]);
                }
                continue;
            }
            if (isSilence(wav, index, sampleSize)) {
                if (!prevSilence) {
                    for (int i = 0; i < sampleSize / 2; i++) {
                        trimmedSound.add(wav[index + i]);
                    }
                    prevSilence = true;
                }
            } else {
                prevSilence = false;
                for (int i = 0; i < sampleSize; i++) {
                    trimmedSound.add(wav[index + i]);
                }
            }
            index += sampleSize;
        }
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
    
}
