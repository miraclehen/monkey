package com.jackson.monkey.entity;

/**
 * author: hd
 * since: 2017/12/15
 *
 * 拍照策略
 */
public class CaptureStrategy {

    public final boolean isPublic;
    public final String authority;

    public CaptureStrategy(boolean isPublic, String authority) {
        this.isPublic = isPublic;
        this.authority = authority;
    }
}
