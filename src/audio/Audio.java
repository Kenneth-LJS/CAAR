package audio;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import utils.ByteUtils;

public class Audio extends IAudio<Float> {

	private int bytesPerSample;
	private int frameSize;
	
	public Audio(String filename) {
		try {
			audio = AudioSystem.getAudioInputStream(new File(filename));
			bytesPerSample = audio.getFormat().getSampleSizeInBits() / 8;
			frameSize = audio.getFormat().getFrameSize();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public Float read() {
		try {
			return (float) audio.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1f;
	}

	public int read(Float[] f) {
		return read(f, 0, f.length);
	}

	public int read(Float[] f, int off, int len) {
		try {
			/*int count = 0;
			byte[] buffer = new byte[4];
			while (len > 2 && audio.read(buffer) != -1) {
				f[count] = (float) ByteUtils.bytesToShort(buffer, 0, false);
				f[count + 1] = (float) ByteUtils.bytesToShort(buffer, 2, false);
				count += 2;
			}
			return count;*/
			len *= bytesPerSample;
			len -= len % 4;
			byte[] buffer = new byte[len];
			int bytesRead = audio.read(buffer, off, len);
			for(int i = 0; i < f.length; i++){
				f[i] = (float) ByteUtils.bytesToShort(buffer, i * bytesPerSample, false);
			}
			return bytesRead;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}


}
