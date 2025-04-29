package top.flobby.mq.broker.utils;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 字节转换工具
 * @create : 2024-06-12 14:46
 **/

public class ByteConvertUtil {

    /**
     * int 转换为 byte
     *
     * @param value 价值
     * @return {@link byte[] }
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        //32位-24位=8位
        //00000000001 0xFF 16
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * byte 转换为 int
     *
     * @param ary ARY
     * @return int
     */
    public static int bytesToInt(byte[] ary) {
        int value;
        value = (int) ((ary[0] & 0xFF)
                | ((ary[1] << 8) & 0xFF00)
                | ((ary[2] << 16) & 0xFF0000)
                | ((ary[3] << 24) & 0xFF000000));
        return value;
    }

    /**
     * 从指定位置阅读len长度的byte
     *
     * @param source 源
     * @param pos    POS位置
     * @param len    莱恩
     * @return {@link byte[] }
     */
    public static byte[] readInPos(byte[] source, int pos, int len) {
        byte[] result = new byte[len];
        for (int i = pos, j = 0; i < pos + len; i++) {
            result[j++] = source[i];
        }
        return result;
    }
}
