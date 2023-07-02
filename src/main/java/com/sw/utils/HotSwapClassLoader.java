package com.sw.utils;

/**
 * @author Wang Hao
 * @date 2023/7/2 18:52
 * @description
 */
public class HotSwapClassLoader extends ClassLoader {

    public HotSwapClassLoader() {
        super(HotSwapClassLoader.class.getClassLoader());
    }

    public Class loadByte(byte[] classBytes) {
        return defineClass(null, classBytes, 0, classBytes.length);
    }
}
