package experimental;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import utils.ByteUtils;

public abstract class SoundTemplate {
    private AudioInputStream stream;
	public SoundTemplate(AudioFormat format){
		try {
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
	        stream = new AudioInputStream((TargetDataLine) targetLine){
	            public int read(byte[] b, int off, int len) {
	                return run(b, off, len);
	            }
	        };
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public AudioInputStream getStream(){
		return stream;
	}
	
	public abstract int run(byte[] b, int off, int len);
}
