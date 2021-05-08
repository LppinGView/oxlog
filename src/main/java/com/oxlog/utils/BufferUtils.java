package com.oxlog.utils;

public class BufferUtils {

    /**
     * 缓存行64对齐
     */
    public static int marginToBuffer(int len) {
        if ((len & 63) != 0) {
            len &= ~63;
            len += 64;
        }
        return len;
    }
}
