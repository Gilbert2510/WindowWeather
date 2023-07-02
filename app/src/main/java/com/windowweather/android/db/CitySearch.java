package com.windowweather.android.db;

import org.litepal.crud.LitePalSupport;

public class CitySearch extends LitePalSupport {
    private int id;
    /**
     * id
     * 名称
     * 上级城市
     * 所属行政区
     * 所属国家
     * 区域名称连缀
     */
    private String cityId;
    private String cityName;
    private String cityAdm2;
    private String cityAdm1;
    private String cityCountry;
    private String areaName;

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

    public String getCityAdm2() {
        return cityAdm2;
    }

    public void setCityAdm2(String cityAdm2) {
        this.cityAdm2 = cityAdm2;
    }

    public String getCityAdm1() {
        return cityAdm1;
    }

    public void setCityAdm1(String cityAdm1) {
        this.cityAdm1 = cityAdm1;
    }

    public String getCityCountry() {
        return cityCountry;
    }

    public void setCityCountry(String cityCountry) {
        this.cityCountry = cityCountry;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
