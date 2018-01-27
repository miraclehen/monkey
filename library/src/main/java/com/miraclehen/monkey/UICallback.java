package com.miraclehen.monkey;

import com.miraclehen.monkey.entity.Album;
import com.miraclehen.monkey.entity.MediaItem;

/**
 * author: miraclehen
 * since: 2018/1/19
 */
public interface UICallback {

    /**
     * 当小图被点击
     *
     * @param album           小图所在相册文件夹
     * @param item            MediaItem实体
     * @param adapterPosition 在adapter中的位置
     */
    void onMediaClick(Album album, MediaItem item, int adapterPosition);

    /**
     * 更新底部工具条显示的选中数量
     */
    void updateBottomBarCount();

    /**
     * 启动拍摄
     * @param captureType 拍摄类型
     */
    void startCapture(CaptureType captureType);

}
