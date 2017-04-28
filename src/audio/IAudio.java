package audio;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public abstract class IAudio<E> {

	protected AudioInputStream audio;

    public abstract int read(E[] b);
    public abstract int read(E[] b, int off, int len);
    public abstract E read();
    
    public AudioFormat getFormat(){
    	return audio.getFormat();
    }
    
	public int available() {
		try {
			return audio.available();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public void close() {
		try {
			audio.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long skip(long n) {
		try {
			return audio.skip(n);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
    
}
