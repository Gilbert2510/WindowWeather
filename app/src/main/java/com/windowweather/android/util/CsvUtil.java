package com.windowweather.android.util;

import android.content.Context;
import android.os.Environment;

import com.windowweather.android.db.CitySearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class CsvUtil {
    private void readDataCsv(Context context) {
        InputStreamReader is=null;
        try {
            is=new InputStreamReader(context.getAssets().open("China-City-List-latest.csv"));
            BufferedReader reader=new BufferedReader(is);
            reader.readLine();
            String line="";
            while((line=reader.readLine())!=null) {
                String buffer[]=line.split(",");
                CitySearch search=new CitySearch();
                String name=buffer[2];
                String adm2=buffer[9];
                String adm1=buffer[7];
                String country=buffer[5];
                search.setCityId(buffer[0]);
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
