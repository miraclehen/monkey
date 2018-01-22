package com.miraclehen.monkey.listener;

import com.miraclehen.monkey.entity.MediaItem;

import java.util.List;


/**
 * author: hd
 * since: 2018/1/8
 * <p>
 * 获取指定的MediaItem之后回调相应的方法
 */
public class CatchSpecMediaItemCallback {

    /**
     * 获取日期最新的一条数据
     */
    public interface newestCallback {
        void catched(MediaItem mediaItem);
    }

    /**
     * 获取指定日期区间的数据
     */
    public interface dateCallback{
        void catched(List<MediaItem> mediaItemList);
    }


}
