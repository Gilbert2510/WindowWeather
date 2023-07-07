package com.windowweather.android.util;

import java.util.Calendar;

public class CurrentDateUtils {
    /**
     * 获取系统的当前日期和时间
     * 年
     * 月
     * 日
     * 时
     * 分
     * 秒
     */
    static Calendar calendar = Calendar.getInstance();

    public static int getCurrentYear() {
        return calendar.get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        return calendar.get(Calendar.MONTH);
    }

    public static int getCurrentDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getCurrentMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    public static int getCurrentSecond() {
        return calendar.get(Calendar.SECOND);
    }
}
