package com.miraclehen.monkey.listener;

import com.miraclehen.monkey.entity.MediaItem;
import com.miraclehen.monkey.ui.widget.MediaGrid;

/**
 * author: miraclehen
 * since: 2018/1/24
 * <p>
 * 自定义生成item布局回调
 */
public interface InflateItemViewCallback {

    /**
     * 自定义生成item布局回调
     * @param mediaItem MediaItem实体
     * @param mediaGrid MediaGrid布局
     */
    void callback(MediaItem mediaItem, MediaGrid mediaGrid);
}
