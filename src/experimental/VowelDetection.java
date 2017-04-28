package experimental;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jtransforms.fft.FloatFFT_1D;

import utils.ByteUtils;
import utils.GraphUtils;

public class VowelDetection {

    public static void main(String args[]) {
        String[] files = new String[] { "ah", "eh", "ch", "ee", "oh", "u", "b", "ck", "t" };
        //files = new String[] { "ee" };
        for (String file : files) {
            try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(file + ".wav"));
//                System.out.println("Encoding: " + audioIn.getFormat().getEncoding().toString());
//                System.out.println("Sample size: " + audioIn.getFormat().getSampleSizeInBits());
//                System.out.println("Frame size: " + audioIn.getFormat().getFrameSize());
    //            Clip clip = AudioSystem.getClip();
    //            clip.open(audioIn);
    //            clip.start();
                byte[] buffer = new byte[4];
                int i = 0;
                int count = 0;
                int totalSamples = audioIn.available() / 2;
                float[] samples = new float[totalSamples];
                while (audioIn.read(buffer) != -1) {
    //                System.out.println(ByteUtils.bytesToShort(buffer, 0, false));
    //                System.out.println(ByteUtils.bytesToShort(buffer, 2, false));
                    samples[count] = ByteUtils.bytesToShort(buffer, 0, false);
                    samples[count + 1] = ByteUtils.bytesToShort(buffer, 2, false);
                    count += 2;
                    //System.out.println(ByteUtils.bytesToShort(buffer, 0, false));
    //                if (i++ > 10) {
    //                    break;
    //                }
                }
                //GraphUtils.dataToGraph(samples, "test1.bmp");
                FloatFFT_1D fft = new FloatFFT_1D(samples.length);
                fft.realForward(samples);
                //GraphUtils.dataToGraph(samples, "test2.bmp");
                samples = filterRealComponent(samples);
                for (int j = 0; j < samples.length; j++) {
                    samples[j] = Math.abs(samples[j]);
                }
                GraphUtils.dataToGraph(samples, "file - " + file + ".bmp");
                System.out.println(file + " : " + calcFormula(samples));
//                System.out.println("Counted samples: " + count);
//                System.out.println("Total samples: " + totalSamples);
                audioIn.close();
            } catch (UnsupportedAudioFileException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        
//        try {
//            float[] vals = new float[201];
//            for (int i = 0; i < vals.length; i++) {
//                vals[i] = (float)
//                ( 1 * 0 * Math.sin(i * Math.PI / vals.length * 2)
//                + 10 * Math.sin(i * Math.PI / vals.length * 4)
//                + 3 * 0 * Math.sin(i * Math.PI / vals.length * 12)
//                + 4 * 0 * Math.sin(i * Math.PI / vals.length * 100));
//            }
//            GraphUtils.dataToGraph(vals, "out1.bmp");
//            FloatFFT_1D fft = new FloatFFT_1D(vals.length);
//            fft.realForward(vals);
//            GraphUtils.dataToGraph(vals, "out2.bmp");
//            vals = filterRealComponent(vals);
//            GraphUtils.dataToGraph(vals, "out3.bmp");
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        System.out.println("Done.");
    }
    

    
    public static float calcFormula(float[] in) {
        float[] arr = new float[in.length];
        System.arraycopy(in, 0, arr, 0, in.length);
        float max = Float.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            max = Math.max(max, arr[i]);
        }
        max /= 20;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < max) {
                arr[i] = 0;
            }
        }
        int count = 0;
        while (findTriangle(arr)) {
            count++;
        } 
        return (float)count;
//        try {
//            GraphUtils.dataToGraph(arr, "formula2.bmp");
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        // variance
//        double sum = 0;
//        for (int i = 0; i < in.length; i++) {
//            sum += in[i] * i;
//        }
//        float mean = (float)(sum / in.length);
//        sum = 0;
//        for (int i = 0; i < in.length; i++) {
//            sum += Math.pow(mean - in[i], 2);
//        }
//        return (float)(sum / in.length);
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
        for (int i = Math.max(maxIndex - curWidth, 0); i <= peakIndex + curWidth; i++) {
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
