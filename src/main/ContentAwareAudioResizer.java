package main;

import java.awt.EventQueue;
import java.io.IOException;
import java.lang.reflect.Array;

import org.jtransforms.fft.FloatFFT_1D;

import gui.SetupForm;
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
        try {
            float[] vals = new float[201];
            for (int i = 0; i < vals.length; i++) {
                vals[i] = (float)
                ( 1 * 0 * Math.sin(i * Math.PI / vals.length * 2)
                + 10 * Math.sin(i * Math.PI / vals.length * 4)
                + 3 * 0 * Math.sin(i * Math.PI / vals.length * 12)
                + 4 * 0 * Math.sin(i * Math.PI / vals.length * 100));
            }
            GraphUtils.dataToGraph(vals, "out1.bmp");
            FloatFFT_1D fft = new FloatFFT_1D(vals.length);
            fft.realForward(vals);
            GraphUtils.dataToGraph(vals, "out2.bmp");
            vals = filterRealComponent(vals);
            GraphUtils.dataToGraph(vals, "out3.bmp");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Done.");
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
