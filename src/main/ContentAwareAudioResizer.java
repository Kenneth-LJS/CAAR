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

import org.jtransforms.fft.FloatFFT_1D;

import gui.SetupForm;
import utils.ByteUtils;
import utils.GraphUtils;

public class ContentAwareAudioResizer {

    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SetupForm frame = new SetupForm();
                    // todo: add listeners
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
}
