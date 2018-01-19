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
     */
    interface OnMediaClickListener {
        void onMediaClick(Album album, MediaItem item, int adapterPosition);
    }

    interface CheckStateListener {
        void onUpdate();
    }

    /**
     * 加载中对话框回调
     */
    interface LoadingDialogCallback {
        /**
         * 显示加载中对话框
         */
        void showDialog();

        /**
         * 对话框消失
         */
        void dismissDialog();
    }

}
