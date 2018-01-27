package com.miraclehen.monkey.listener;

import com.miraclehen.monkey.entity.MediaItem;

/**
 * author: miraclehen
 * since: 2018/1/22
 *
 * 当某个Item被勾选或者取消勾选监听器
 */
public interface OnItemCheckChangeListener {

    /**
     * 当某个Item被勾选
     * @param mediaItem 目标Item
     * @param check true为被勾选，false则取消勾选
     */
    void onCheck(MediaItem mediaItem,boolean check);
}
