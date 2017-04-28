package experimental;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class SoundThresholdDetection {

    public static void main(String args[]) throws Exception {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("sample.wav"));
        
    }
    
}
