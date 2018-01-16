package com.miraclehen.monkey.listener;

import android.support.annotation.Nullable;

import com.miraclehen.monkey.entity.MediaItem;


/**
 * author: hd
 * since: 2018/1/8
 * <p>
 * 获取某个位置指定的MediaItem
 */
public interface CatchSpePositionCallback {

    void catched(int position, @Nullable MediaItem mediaItem);
}
