package com.windowweather.android.util;

import android.content.Context;

import com.windowweather.android.db.CityHot;
import com.windowweather.android.db.CitySearch;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CsvUtils {
    /**
     * 读取应用自带的csv文件
     * @param context
     */
    public static void readCityHotCsv(Context context) {
        InputStreamReader is;
        try {
            is=new InputStreamReader(context.getResources().getAssets().open("City_hot_list.csv"));
            BufferedReader reader=new BufferedReader(is);
            reader.readLine();
            String line="";
            while((line=reader.readLine())!=null) {
                String[] buffer =line.split(",");
                CityHot cityHot=new CityHot();
                String id=buffer[0];
                String name=buffer[2];
                cityHot.setCityId(id);
                cityHot.setCityName(name);
                cityHot.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void readDataCsv(Context context) {
        InputStreamReader is;
        int tag=1;
        try {
            is=new InputStreamReader(context.getResources().getAssets().open("China_city_list_latest.csv"));
            BufferedReader reader=new BufferedReader(is);
            reader.readLine();
            String line="";
            while((line=reader.readLine())!=null) {
                String[] buffer =line.split(",");
                CitySearch search=new CitySearch();
                String name="";
                String adm2="";
                String adm1="";
                String country="";
                String id=buffer[0];
                if(id.equals("101320101")) {
                    tag=2;
                } else if(id.equals("101340201")) {
                    tag=3;
                }
                if(tag==1) {
                    name=buffer[2];
                    adm2=buffer[9];
                    adm1=buffer[7];
                    country=buffer[5];
                } else if(tag==2) {
                    name=buffer[2];
                    adm2=buffer[10];
                    adm1=buffer[8];
                    country=buffer[6];
                } else {
                    name=buffer[2];
                    adm2=buffer[11];
                    adm1=buffer[9];
                    country=buffer[6];
                }
                search.setCityId(id);
                search.setCityName(name);
                search.setCityAdm2(adm2);
                search.setCityAdm1(adm1);
                search.setCityCountry(country);
                search.setAreaName(name+"-"+adm2+"-"+adm1+"-"+country);
                search.save();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
