package com.miraclehen.monkey.utils;

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
     * 根据时间位数做判断
     * <p>
     * 最大秒数 用作毫秒时 ：9999999999 = 1970/4/27 1:46:39
     * 最小毫秒数 当做秒来处理 10000000000 = 2286/11/21 1:46:40
     *
     * @param time
     * @return
     */
    public static long timeToMs(long time) {
        //位数小于等于10位数，当做秒来处理，否则当初毫秒来处理
        return String.valueOf(time).length() <= 10 ? time * 1000 : time;
    }
}
