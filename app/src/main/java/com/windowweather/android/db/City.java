package com.windowweather.android.db;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {
    private int id;
    /**
     * 城市情况
     * id
     * 名称
     * 天气预报网页链接
     */
    private String cityId;
    private String cityName;
    private String fxLink;

    /**
     * 城市当前天气情况
     * 观测时间
     * 体感温度
     * 温度
     * 天气情况
     */
    private String obsTime;
    private String feelsLike;
    private String nowTemp;
    private String nowText;
    private String nowIcon;
    private String nowWindDir;
    private String nowWindScale;
    private String nowHumidity;
    private String nowPressure;
    private String nowVis;
    private String nowCloud;

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

    public String getFxLink() {
        return fxLink;
    }

    public void setFxLink(String fxLink) {
        this.fxLink = fxLink;
    }

    public String getObsTime() {
        return obsTime;
    }

    public void setObsTime(String obsTime) {
        this.obsTime = obsTime;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(String feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getNowTemp() {
        return nowTemp;
    }

    public void setNowTemp(String nowTemp) {
        this.nowTemp = nowTemp;
    }

    public String getNowText() {
        return nowText;
    }

    public void setNowText(String nowText) {
        this.nowText = nowText;
    }

    public String getNowIcon() {
        return nowIcon;
    }

    public void setNowIcon(String nowIcon) {
        this.nowIcon = nowIcon;
    }

    public String getNowWindDir() {
        return nowWindDir;
    }

    public void setNowWindDir(String nowWindDir) {
        this.nowWindDir = nowWindDir;
    }

    public String getNowWindScale() {
        return nowWindScale;
    }

    public void setNowWindScale(String nowWindScale) {
        this.nowWindScale = nowWindScale;
    }

    public String getNowHumidity() {
        return nowHumidity;
    }

    public void setNowHumidity(String nowHumidity) {
        this.nowHumidity = nowHumidity;
    }

    public String getNowPressure() {
        return nowPressure;
    }

    public void setNowPressure(String nowPressure) {
        this.nowPressure = nowPressure;
    }

    public String getNowVis() {
        return nowVis;
    }

    public void setNowVis(String nowVis) {
        this.nowVis = nowVis;
    }

    public String getNowCloud() {
        return nowCloud;
    }

    public void setNowCloud(String nowCloud) {
        this.nowCloud = nowCloud;
    }
}
