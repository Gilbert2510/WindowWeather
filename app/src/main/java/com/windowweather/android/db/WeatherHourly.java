package com.windowweather.android.db;

import org.litepal.crud.LitePalSupport;

public class WeatherHourly extends LitePalSupport {
    private int id;

    /**
     * 城市每小时天气情况
     * id
     * 名称
     * 观测时间
     * 温度：℃
     * 天气情况
     * 降水概率：%
     * 降水量：毫米
     */
    private String cityId;
    private String cityName;
    private String hourlyFxTime;
    private String hourlyTemp;
    private String hourlyText;
    private String hourlyPop;
    private String hourlyPrecip;
    private String hourlyIcon;
    private String hourlyPressure;

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

    public String getHourlyFxTime() {
        return hourlyFxTime;
    }

    public void setHourlyFxTime(String hourlyFxTime) {
        this.hourlyFxTime = hourlyFxTime;
    }

    public String getHourlyTemp() {
        return hourlyTemp;
    }

    public void setHourlyTemp(String hourlyTemp) {
        this.hourlyTemp = hourlyTemp;
    }

    public String getHourlyText() {
        return hourlyText;
    }

    public void setHourlyText(String hourlyText) {
        this.hourlyText = hourlyText;
    }

    public String getHourlyPop() {
        return hourlyPop;
    }

    public void setHourlyPop(String hourlyPop) {
        this.hourlyPop = hourlyPop;
    }

    public String getHourlyPrecip() {
        return hourlyPrecip;
    }

    public void setHourlyPrecip(String hourlyPrecip) {
        this.hourlyPrecip = hourlyPrecip;
    }

    public String getHourlyIcon() {
        return hourlyIcon;
    }

    public void setHourlyIcon(String hourlyIcon) {
        this.hourlyIcon = hourlyIcon;
    }

    public String getHourlyPressure() {
        return hourlyPressure;
    }

    public void setHourlyPressure(String hourlyPressure) {
        this.hourlyPressure = hourlyPressure;
    }
}
