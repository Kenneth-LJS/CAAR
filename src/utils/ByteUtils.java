package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtils {

    public static byte[] intToBytes(int i, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[4]);
        buffer.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(i);
        return buffer.array();
    }
    
    public static float bytesToInt(byte[] b, int position, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(b, position, 4);
        buffer.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
    }
    
    public static byte[] floatToBytes(float f, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[4]);
        buffer.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        buffer.putFloat(f);
        return buffer.array();
    }
    
    public static float bytesToFloat(byte[] b, int position, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(b, position, 4);
        buffer.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        return buffer.getFloat();
    }
    
    public static byte[] doubleToBytes(double d, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[8]);
        buffer.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        buffer.putDouble(d);
        return buffer.array();
    }
    
    public static double bytesToDouble(byte[] b, int position, boolean bigEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(b, position, 8);
        buffer.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        return buffer.getDouble();
    }
    
}
