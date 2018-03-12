package com.miraclehen.monkey.utils;

import android.database.Cursor;

import com.miraclehen.monkey.entity.Album;
import com.miraclehen.monkey.entity.SelectionSpec;
import com.miraclehen.monkey.ui.adapter.CursorBean;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * author: hd
 * since: 2018/1/9
 */
public class DateTimeUtil {

    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }

    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }

    /**
     * 将传入的时间，转换为毫秒。
     * <p>
     * 1514736000000 为2018年一月一号0点0分0秒
     *
     * @param time
     * @return
     */
    public static long timeToMs(long time) {
        long cTimeMis = System.currentTimeMillis() > 1514736000000L ? System.currentTimeMillis() : 1514736000000L;
        return time * 1000 > cTimeMis ? time : time * 1000;
    }

    /**
     * 计算自动滚动到相应日期位置
     *
     * @param album
     * @param dataList
     * @param cursor
     */
    public static int calAutoScrollDatePosition(Album album, List<CursorBean> dataList, Cursor cursor) {
        if (dataList.isEmpty()) {
            return -1;
        }
        ArrayList<CursorBean> data = new ArrayList<>();
        data.addAll(dataList);

        long targetDate = SelectionSpec.getInstance().autoScrollDate;
        boolean captureFlag = SelectionSpec.getInstance().isCapture();

        CursorBean beanA = null;
        CursorBean beanB = null;
        int result = -1;
        for (CursorBean bean : data) {
            if (!bean.isDateView()) {
                continue;
            }
            if (captureFlag) {
                captureFlag = false;
                continue;
            }
            if (bean.getDateValue() == SelectionSpec.getInstance().autoScrollDate) {
                return bean.getAdapterPosition();
            }
            if (beanA == null) {
                beanA = bean;
                if (targetDate > beanA.getDateValue()) {
                    return bean.getAdapterPosition();
                }
                continue;
            }
            if (beanB == null) {
                beanB = bean;
                if (targetDate > beanB.getDateValue() && targetDate < bean.getDateValue()) {
                    result = compare(beanA, beanB, targetDate).getAdapterPosition();
                    break;
                }
                continue;
            }

            beanA = beanB;
            beanB = bean;
            if (targetDate > beanB.getDateValue() && targetDate < beanA.getDateValue()) {
                result = compare(beanA, beanB, targetDate).getAdapterPosition();
                break;
            }
        }
        return result;
    }

    private static CursorBean compare(CursorBean beanA, CursorBean beanB, long target) {
        long at = beanA.getDateValue() - target;
        long bt = beanB.getDateValue() - target;
        return at > bt ? beanB : beanA;
    }

}
