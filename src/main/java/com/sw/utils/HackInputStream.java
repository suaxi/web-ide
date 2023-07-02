package com.sw.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Wang Hao
 * @date 2023/7/2 18:20
 * @description 为每一个线程保持一个标准输入流
 */
public class HackInputStream extends InputStream {

    public static final ThreadLocal<InputStream> holdInputStream = new ThreadLocal<>();

    @Override
    public int read() throws IOException {
        return 0;
    }

    public InputStream get() {
        return holdInputStream.get();
    }

    public void set(String systemIn) {
        holdInputStream.set(new ByteArrayInputStream(systemIn.getBytes()));
    }

    @Override
    public void close() {
        holdInputStream.remove();
    }
}
