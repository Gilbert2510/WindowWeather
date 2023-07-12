package com.windowweather.android.db;

import org.litepal.crud.LitePalSupport;

public class WeatherDaily extends LitePalSupport {
    private int id;

    /**
     * 城市每天天气情况
     * id
     * 名称
     * 观测日期
     * 日出时间
     * 日落时间
     * 月出时间
     * 月落时间
     * 月相名称
     * 最高温
     * 最低温
     * 日天气状态id
     * 白天天气状态
     * 夜晚天气状态
     */
    private String cityId;
    private String cityName;
    private String dailyFxDate;
    private String dailySunRise;
    private String dailySunSet;
    private String dailyMoonRise;
    private String dailyMoonSet;
    private String dailyMoonPhase;
    private String dailyMax;
    private String dailyMin;
    private String dailyIconDay;
    private String dailyTextDay;
    private String dailyTextNight;
    private String dailyUvIndex;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDailyFxDate() {
        return dailyFxDate;
    }

    public void setDailyFxDate(String dailyFxDate) {
        this.dailyFxDate = dailyFxDate;
    }

    public String getDailySunRise() {
        return dailySunRise;
    }

    public void setDailySunRise(String dailySunRise) {
        this.dailySunRise = dailySunRise;
    }

    public String getDailySunSet() {
        return dailySunSet;
    }

    public void setDailySunSet(String dailySunSet) {
        this.dailySunSet = dailySunSet;
    }

    public String getDailyMoonRise() {
        return dailyMoonRise;
    }

    public void setDailyMoonRise(String dailyMoonRise) {
        this.dailyMoonRise = dailyMoonRise;
    }

    public String getDailyMoonSet() {
        return dailyMoonSet;
    }

    public void setDailyMoonSet(String dailyMoonSet) {
        this.dailyMoonSet = dailyMoonSet;
    }

    public String getDailyMoonPhase() {
        return dailyMoonPhase;
    }

    public void setDailyMoonPhase(String dailyMoonPhase) {
        this.dailyMoonPhase = dailyMoonPhase;
    }

    public String getDailyMax() {
        return dailyMax;
    }

    public void setDailyMax(String dailyMax) {
        this.dailyMax = dailyMax;
    }

    public String getDailyMin() {
        return dailyMin;
    }

    public void setDailyMin(String dailyMin) {
        this.dailyMin = dailyMin;
    }

    public String getDailyIconDay() {
        return dailyIconDay;
    }

    public void setDailyIconDay(String dailyIconDay) {
        this.dailyIconDay = dailyIconDay;
    }

    public String getDailyTextDay() {
        return dailyTextDay;
    }

    public void setDailyTextDay(String dailyTextDay) {
        this.dailyTextDay = dailyTextDay;
    }

    public String getDailyTextNight() {
        return dailyTextNight;
    }

    public void setDailyTextNight(String dailyTextNight) {
        this.dailyTextNight = dailyTextNight;
    }

    public String getDailyUvIndex() {
        return dailyUvIndex;
    }

    public void setDailyUvIndex(String dailyUvIndex) {
        this.dailyUvIndex = dailyUvIndex;
    }
}
