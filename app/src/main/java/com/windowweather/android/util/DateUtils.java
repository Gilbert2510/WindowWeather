package com.windowweather.android.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
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

    // 将类如 16:04 的字符串转化为固定格式的 Date 对象
    public static Date strToDate(String str) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        String timeStr = currentYear + "-" + (currentMonth + 1) + "-" + currentDay + " " + str + ":00";

        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return timeFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
