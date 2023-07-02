package com.sw.utils;

/**
 * @author Wang Hao
 * @date 2023/7/2 16:31
 * @description
 */
public class ByteUtils {

    /**
     * byte数组转int
     *
     * @param bytes  原数组
     * @param start  开始位置
     * @param length 长度
     * @return
     */
    public static int byte2Int(byte[] bytes, int start, int length) {
        int res = 0;
        int end = start + length;
        for (int i = start; i < end; i++) {
            int current = ((int) bytes[i]) & 0xff;
            current <<= (--length) * 8;
            res += current;
        }
        return res;
    }

    /**
     * int转byte数组
     *
     * @param num    原始值
     * @param length 长度
     * @return
     */
    public static byte[] int2Byte(int num, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[length - i - 1] = (byte) ((num >> (8 * i)) & 0xff);
        }
        return bytes;
    }

    /**
     * byte数组转字符串
     *
     * @param bytes  原数组
     * @param start  开始位置
     * @param length 长度
     * @return
     */
    public static String byte2String(byte[] bytes, int start, int length) {
        return new String(bytes, start, length);
    }

    /**
     * 字符串转byte数组
     *
     * @param str 字符串
     * @return
     */
    public static byte[] string2Byte(String str) {
        return str.getBytes();
    }

    /**
     * 数组替换
     *
     * @param oldBytes     原数组
     * @param offset       偏移量
     * @param length       长度
     * @param replaceBytes 替换数组
     * @return
     */
    public static byte[] byteReplace(byte[] oldBytes, int offset, int length, byte[] replaceBytes) {
        byte[] newBytes = new byte[oldBytes.length + replaceBytes.length - length];
        System.arraycopy(oldBytes, 0, newBytes, 0, offset);
        System.arraycopy(replaceBytes, 0, newBytes, offset, replaceBytes.length);
        System.arraycopy(oldBytes, offset + length, newBytes, offset + replaceBytes.length, oldBytes.length - offset - length);
        return newBytes;
    }
}
