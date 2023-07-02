package com.sw.utils;

/**
 * @author Wang Hao
 * @date 2023/7/2 16:47
 * @description
 */
public class ClassModifier {

    /**
     * class文件中常量池起始偏移量
     */
    private static final int CONSTANT_POOL_COUNT_INDEX = 8;

    /**
     * CONSTANT_UTF8_INFO常量tag
     */
    private static final int CONSTANT_UTF8_INFO = 1;

    /**
     * 常量池中11种常量的长度，CONSTANT_ITEM_LENGTH[tag] 为它的长度
     */
    private static final int[] CONSTANT_ITEM_LENGTH = {-1, -1, -1, 5, 5, 9, 9, 3, 3, 5, 5, 5, 5};

    /**
     * 1个和2个字节的符号数，用来从classBytes数组中取 tag 和 length
     * tag：u1个字节
     * length：u2个字节
     */
    private static final int u1 = 1;
    private static final int u2 = 2;

    /**
     * 要被修改的字节码文件
     */
    private byte[] classByte;

    public ClassModifier(byte[] classByte) {
        this.classByte = classByte;
    }

    /**
     * 从0x00000008开始向后取2个字节，表示的是常量池中常量的个数
     *
     * @return 常量池中常量的个数
     */
    public int getConstantPoolCount() {
        return ByteUtils.byte2Int(classByte, CONSTANT_POOL_COUNT_INDEX, u2);
    }

    /**
     * 替换字节码常量池中 oldStr 为 nweStr
     *
     * @param oldStr 原字符串
     * @param newStr 新字符串
     * @return
     */
    public byte[] modifyUTF8Constant(String oldStr, String newStr) {
        int cpc = this.getConstantPoolCount();
        //真是的常量起始位置
        int offset = CONSTANT_POOL_COUNT_INDEX + u2;
        for (int i = 1; i < cpc; i++) {
            int tag = ByteUtils.byte2Int(classByte, offset, u2);
            if (tag == CONSTANT_UTF8_INFO) {
                int length = ByteUtils.byte2Int(classByte, offset + u1, u2);
                offset += u1 + u2;
                String str = ByteUtils.byte2String(classByte, offset, length);
                if (str.equals(oldStr)) {
                    byte[] strReplaceBytes = ByteUtils.string2Byte(newStr);
                    byte[] intReplaceBytes = ByteUtils.int2Byte(strReplaceBytes.length, u2);
                    //替换新的字符串的长度
                    classByte = ByteUtils.byteReplace(classByte, offset - u2, u2, intReplaceBytes);
                    //替换新的字符串本身
                    classByte = ByteUtils.byteReplace(classByte, offset, length, strReplaceBytes);
                    return classByte;
                } else {
                    offset += length;
                }
            } else {
                offset += CONSTANT_ITEM_LENGTH[tag];
            }
        }
        return classByte;
    }
}
