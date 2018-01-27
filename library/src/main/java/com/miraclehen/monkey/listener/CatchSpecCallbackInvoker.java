package com.miraclehen.monkey.listener;

import android.database.Cursor;

import com.miraclehen.monkey.entity.Album;
import com.miraclehen.monkey.entity.MediaItem;
import com.miraclehen.monkey.entity.SelectionSpec;
import com.miraclehen.monkey.ui.adapter.CursorBean;

import java.util.List;

/**
 * author: miraclehen
 * since: 2018/1/22
 */
public class CatchSpecCallbackInvoker {

    /**
     * 获取日期最新的一条数据
     * @param album
     * @param dataList 排序之后的列表数据
     */
    public static void invokeNewestCallback(Album album, List<CursorBean> dataList, Cursor cursor) {
        SelectionSpec spec = SelectionSpec.getInstance();
        if (spec.catchNewestSpecCallback == null) {
            return;
        }
        if (!album.isAll()) {
            return;
        }
        if (cursor.isClosed()) {
            return;
        }

        //最新一条数据的位置
        int catchPos = 0;
        if (spec.groupByDate) {
            //跳过日期视图
            catchPos++;
            if (dataList.size() == catchPos) {
                return;
            }
        }
        if (spec.isCapture()) {
            //跳过拍摄视图
            catchPos++;
            if (dataList.size() == catchPos) {
                return;
            }
        }

        int reachPos = dataList.get(catchPos).getCursorPosition();
        if (reachPos == -1) {
            return;
        }
        boolean reachable = cursor.moveToPosition(reachPos);
        if (reachable) {
            spec.catchNewestSpecCallback.catched(MediaItem.valueOf(cursor));
        }
        spec.catchNewestSpecCallback = null;
    }

    public static void invokeDateCallback(List<MediaItem> mediaItemList) {

    }
}
