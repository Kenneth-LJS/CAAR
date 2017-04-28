package experimental;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import utils.ByteUtils;

import javax.sound.sampled.AudioFileFormat.Type;

public class SoundGenerator {

    private static final float SAMPLE_RATE = 44100;
    
    public static void main(String[] args) throws IOException {
//        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
//        InputStream is = new ByteArrayInputStream(output);
//        AudioFormat af = new AudioFormat(Encoding.PCM_UNSIGNED, audio.getFormat().getSampleRate(),
//                audio.getFormat().getSampleSizeInBits(), audio.getFormat().getChannels(),
//                audio.getFormat().getFrameSize(), audio.getFormat().getFrameRate(), true);
//        
        long audioLength = (long)Math.round(SAMPLE_RATE * 3);
        if (audioLength % 16 != 0) {
            audioLength += 16 - (audioLength % 16);
        }
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
        AudioInputStream stream = new AudioInputStream(new SineByteStream(audioLength), format, audioLength);
        AudioSystem.write(stream, Type.WAVE, new File("file.wav"));
//        AudioInputStream stream = new AudioInputStream(is, audio.getFormat(), (long) buff2.length);
//        AudioSystem.write(stream, Type.WAVE, new File("file.wav"));
        System.out.println("Done.");
    }
    
    private static class SineDataLine implements TargetDataLine {

        public int available() {
            // TODO Auto-generated method stub
            return 0;
        }

        public void drain() {
            // TODO Auto-generated method stub
            
        }

        public void flush() {
            // TODO Auto-generated method stub
            
        }

        public int getBufferSize() {
            // TODO Auto-generated method stub
            return 0;
        }

        public AudioFormat getFormat() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getFramePosition() {
            // TODO Auto-generated method stub
            return 0;
        }

        public float getLevel() {
            // TODO Auto-generated method stub
            return 0;
        }

        public long getLongFramePosition() {
            // TODO Auto-generated method stub
            return 0;
        }

        public long getMicrosecondPosition() {
            // TODO Auto-generated method stub
            return 0;
        }

        public boolean isActive() {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean isRunning() {
            // TODO Auto-generated method stub
            return false;
        }

        public void start() {
            // TODO Auto-generated method stub
            
        }

        public void stop() {
            // TODO Auto-generated method stub
            
        }

        public void addLineListener(LineListener arg0) {
            // TODO Auto-generated method stub
            
        }

        public void close() {
            // TODO Auto-generated method stub
            
        }

        public Control getControl(javax.sound.sampled.Control.Type arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public Control[] getControls() {
            // TODO Auto-generated method stub
            return null;
        }

        public javax.sound.sampled.Line.Info getLineInfo() {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isControlSupported(javax.sound.sampled.Control.Type arg0) {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean isOpen() {
            // TODO Auto-generated method stub
            return false;
        }

        public void open() throws LineUnavailableException {
            // TODO Auto-generated method stub
            
        }

        public void removeLineListener(LineListener arg0) {
            // TODO Auto-generated method stub
            
        }

        public void open(AudioFormat arg0) throws LineUnavailableException {
            // TODO Auto-generated method stub
            
        }

        public void open(AudioFormat arg0, int arg1) throws LineUnavailableException {
            // TODO Auto-generated method stub
            
        }

        public int read(byte[] arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
            return 0;
        }
        
    }
    
    private static class SineByteStream extends InputStream {
        
        private long lengthLeft;
        private double theta = 0;
        private double stepSize = Math.PI / 100;
        
        public SineByteStream(long length) {
            this.lengthLeft = length + (16 - (length % 16));
        }
        
        @Override
        public int read(byte[] b) {
            return read(b, 0, b.length);
        }
        
        @Override
        public int read(byte[] b, int off, int len) {
            if (lengthLeft == 0) {
                return -1;
            }
            len = b.length - b.length % 2;
            for (int i = 0; i < len; i += 2) {
                byte[] arr = ByteUtils.shortToBytes((short)Math.floor(Math.sin(theta) * 3000), true);
                b[off + i] = arr[0];
                b[off + i + 1] = arr[1];
                theta = (theta + stepSize) % (2 * Math.PI);
                lengthLeft -= 16;
            }
            return len;
        }

        @Override
        public int read() throws IOException {
            throw new IOException("cannot read a single byte if frame size > 1");
        }
    }
    
}
