package experimental;

import javax.sound.sampled.AudioFormat;

import utils.ByteUtils;

public class SineSound extends SoundTemplate{

    private long lengthLeft;
    private double stepSize;
    
    private double theta = 0;
    
	public SineSound(AudioFormat format, long length, double stepSize) {
		super(format);
		this.lengthLeft = length;
		this.stepSize = stepSize;
	}

	@Override
	public int run(byte[] b, int off, int len) {
        if (lengthLeft < 0) {
            return -1;
        }
        len = b.length - b.length % 2;
        for (int i = 0; i < len; i += 2) {
            byte[] arr = ByteUtils.shortToBytes((short)Math.floor(Math.sin(theta) * 3000), true);
            b[off + i] = arr[0];
            b[off + i + 1] = arr[1];
            theta = (theta + stepSize) % (2 * Math.PI);
        }
        lengthLeft -= len;
        return len;
	}

}
