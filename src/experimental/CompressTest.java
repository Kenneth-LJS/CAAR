package experimental;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import utils.ByteUtils;

public class CompressTest {
	public static void compressAndSave(String fileName, float comp) {

		try {

			AudioInputStream audio = AudioSystem.getAudioInputStream(new File(fileName));
			int totalSamples = audio.available();
			byte[] buff = new byte[totalSamples];
			audio.read(buff);
			float[] fbuff = bytesToFloat(buff);
			float[] buff2 = interpolate(fbuff, comp);
			byte[] output = floatsToByte(buff2);
			AudioFormat format = audio.getFormat();
			InputStream is = new ByteArrayInputStream(output);
			AudioFormat af = new AudioFormat(Encoding.PCM_UNSIGNED, audio.getFormat().getSampleRate(),
					audio.getFormat().getSampleSizeInBits(), audio.getFormat().getChannels(),
					audio.getFormat().getFrameSize(), audio.getFormat().getFrameRate(), true);
			AudioInputStream stream = new AudioInputStream(is, audio.getFormat(), (long) buff2.length);
			File file = new File("file.wav");
			AudioSystem.write(stream, Type.WAVE, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static float[] bytesToFloat(byte[] buff) {
		float[] fbuff = new float[buff.length / 2];
		for (int i = 0; i < fbuff.length; i++) {
			fbuff[i] = buff[i * 2] + buff[i * 2 + 1] * 256;
		}
		return fbuff;
	}

	private static byte[] floatsToByte(float[] buff) {
		byte[] bbuff = new byte[buff.length * 2];
		for (int i = 0; i < buff.length; i++) {
			bbuff[2 * i] = (byte) (buff[i] % 256);
			bbuff[2 * i + 1] = (byte) (buff[i] / 256);
		}
		return bbuff;
	}

	private static float[] interpolate(float[] buff, float comp) {
		float[] buff2 = new float[buff.length];
		int newTotal = (int) ((float) buff.length / comp);
		for (int i = 0; i < newTotal; i++) {
			float index = comp * i;
			float b1 = buff[(int) Math.floor(index)];
			float b2 = buff[Math.min((int) Math.ceil(index), buff.length - 1)];
			float rem = (index + 0.0000001f) % 1;
			float newVal = (b1 + (b2 - b1) * rem);
			buff2[i] = newVal;
		}

		return buff2;
	}
}
