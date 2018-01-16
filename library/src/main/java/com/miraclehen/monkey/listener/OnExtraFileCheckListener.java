package com.miraclehen.monkey.listener;


import com.miraclehen.monkey.entity.MediaItem;

/**
 * author: hd
 * since: 2017/12/22
 * <p>
 * 当外部传进来的extra文件被点击
 */
public interface OnExtraFileCheckListener {

    void onCheck(MediaItem mediaItem);

}
