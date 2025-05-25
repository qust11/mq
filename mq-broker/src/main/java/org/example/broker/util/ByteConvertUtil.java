package org.example.broker.util;

/**
 * @author qushutao
 * @since 2025/5/25-23:54
 */
public class ByteConvertUtil {

    public static byte[] intToByteArray(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }
    public static int byteArrayToInt(byte[] src) {
        int value;
        value = ((src[3] & 0xFF) << 24)
                | ((src[2] & 0xFF) << 16)
                | ((src[1] & 0xFF) << 8)
                | (src[0] & 0xFF);
        return value;
    }

}
