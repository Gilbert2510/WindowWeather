package com.windowweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qweather.sdk.bean.Basic;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;
import com.windowweather.android.adapter.SearchAdapter;
import com.windowweather.android.adapter.HotCityAdapter;
import com.windowweather.android.db.City;
import com.windowweather.android.db.CityHot;
import com.windowweather.android.db.CitySearch;

import org.litepal.LitePal;

import java.util.List;

public class CitySearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private List<CitySearch> searchList;
    private List<CityHot> cityHotList;
    private SearchAdapter searchAdapter;
    //搜索界面控件
    private ListView searchListView;
    private TextView searchTitle;
    private RecyclerView hotRecycleView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        /**
         * 获取组件
         */
        SearchView citySearchView = findViewById(R.id.city_search_searchView);
        Button citySearchBack = findViewById(R.id.city_search_back);
        searchListView = findViewById(R.id.activity_search_listview);
        hotRecycleView = findViewById(R.id.activity_search_recyclerview);
        searchTitle = findViewById(R.id.activity_search_title);

        /**
         * 设置上方返回按钮监听器
         * 当点击时返回上一个活动
         */
        citySearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * 显示搜索城市列表
         * 响应搜索系统action进行查找
         * 此时显示从服务器搜索得到的数据
         */
        searchList = LitePal.findAll(CitySearch.class);
        searchAdapter = new SearchAdapter(this, searchList);
        searchListView.setAdapter(searchAdapter);
        searchListView.setTextFilterEnabled(true);
        //设置该SearchView默认是否自动缩小为图标
        citySearchView.setIconifiedByDefault(false);
        citySearchView.setOnQueryTextListener(this);

        /**
         * 设置搜索城市列表的监听器
         * 当点击时返回管理界面
         */
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CitySearch search = (CitySearch) parent.getAdapter().getItem(position);
                String cityId = search.getCityId();
                String cityName = search.getCityName();
                List<City> list = LitePal.where("cityId = ?", cityId).find(City.class);
                if (list.size() > 0) {
                    //此时点击的城市中数据库已经存储
                    Log.d("choose","启动");
                    setResult(RESULT_OK);
                } else {
                    Intent intent=getIntent();
                    String str=intent.getStringExtra("main");
                    if(str!=null) {
                        QWeather.getWeatherNow(CitySearchActivity.this, cityId, new QWeather.OnResultWeatherNowListener() {
                            public static final String TAG = "weather_now";

                            @Override
                            public void onError(Throwable throwable) {
                                Log.i(TAG, "on error: ", throwable);
                                //System.out.println("Weather Now Error:"+new Gson());
                                Looper.prepare();
                                Toast.makeText(CitySearchActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                            @Override
                            public void onSuccess(WeatherNowBean weatherNowBean) {
                                Log.i(TAG, "getWeatherNow onSuccess: " + new Gson().toJson(weatherNowBean));
                                //System.out.println("获取当前天气成功"+new Gson().toJson(weatherNowBean));
                                if (Code.OK == weatherNowBean.getCode()) {
                                    WeatherNowBean.NowBaseBean bean = weatherNowBean.getNow();
                                    Basic basic = weatherNowBean.getBasic();
                                    City city;
                                    //否则添加新的城市项
                                    city = new City();
                                    city.setCityName(cityName);
                                    city.setCityId(cityId);
                                    city.setFxLink(basic.getFxLink());
                                    city.setObsTime(bean.getObsTime());
                                    city.setFeelsLike(bean.getFeelsLike());
                                    city.setNowTemp(bean.getTemp());
                                    city.setNowText(bean.getText());
                                    city.setNowIcon(bean.getIcon());
                                    city.save();
                                    Intent intent=new Intent(view.getContext(), WeatherActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
//                        Utility.queryWeatherNow(view.getContext(), cityId, cityName, false);

                    } else {
                        //数据库没有存储，则存储
                        intent=new Intent(CitySearchActivity.this, CityManageActivity.class);
                        intent.putExtra("searchId",cityId);
                        intent.putExtra("searchName",cityName);
                        setResult(RESULT_FIRST_USER,intent);
                    }
                }
                finish();
            }
        });

        /**
         * 显示热门城市列表
         * 当查询数据库不为空是直接查询热门城市数据库并显示，否则显示查询服务器返回的数据
         */
        cityHotList = LitePal.findAll(CityHot.class);
        if (cityHotList == null) {
            queryCityHot(this);
        }
        cityHotList = LitePal.findAll(CityHot.class);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        hotRecycleView.setLayoutManager(gridLayoutManager);
        HotCityAdapter hotAdapter = new HotCityAdapter(cityHotList);
        hotRecycleView.setAdapter(hotAdapter);

        /**
         * 热门城市列表的监听器
         * 当点击时返回管理界面
         */
        hotAdapter.setOnItemClickListener(new HotCityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String id = cityHotList.get(position).getCityId();
                Intent intent = new Intent(view.getContext(), CityManageActivity.class);
                intent.putExtra("searchCityId", id);
                startActivity(intent);
                finish();
            }
        });

    }

    /**
     * 用户输入字符时激发该方法
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        showSearch();
        //如果newText是长度为0的字符串
        if (TextUtils.isEmpty(newText)) {
            //清除ListView的过滤
            searchListView.clearTextFilter();
            searchAdapter.getFilter().filter("");
            hideSearch();
        } else {
            //使用用户输入的内容对ListView的列表项进行过滤
            //searchListView.setFilterText(newText);
            searchAdapter.getFilter().filter(newText);
        }
        return true;
    }

    /**
     * 单击搜索按钮时激发该方法
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

//    /**
//     * 连接服务器时显示对话框
//     */
//    private void showWaitingDialog() {
//        //toDo
//    }

//    /**
//     * 显示查询城市的结果
//     */
//    public void showCityInfo(GeoBean geoBean) {
//        List<GeoBean.LocationBean> locationBeanList=geoBean.getLocationBean();
//        CitySearch city=new CitySearch();
//        //第一次搜索服务器的数据存入数据库
//        for(GeoBean.LocationBean bean:locationBeanList) {
//            String name=bean.getName();
//            String adm2=bean.getAdm2();
//            String adm1=bean.getAdm1();
//            String country=bean.getCountry();
//            String areaName=name+"-"+adm2+"-"+adm1+"-"+country;
//            city.setCityId(bean.getId());
//            city.setCityName(name);
//            city.setCityAdm2(adm2);
//            city.setCityAdm1(adm1);
//            city.setCityCountry(country);
//            city.setAreaName(areaName);
//            searchList.add(city);
//            city.save();
//        }
//    }
//
//    /**
//     * 查询城市
//     */
//    public void queryCitySearch(Context context, String location) {
//        QWeather.getGeoCityLookup(context, location, new QWeather.OnResultGeoListener() {
//            public static final String TAG = "city_search";
//
//            @Override
//            public void onError(Throwable throwable) {
//                Log.i(TAG, "on error: ", throwable);
//                //System.out.println("City Search Error:"+new Gson());
//                Toast.makeText(context, "连接服务器失败", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onSuccess(GeoBean geoBean) {
//                Log.i(TAG, "getCity onSuccess: " + new Gson().toJson(geoBean));
//                //System.out.println("获取城市成功"+new Gson().toJson(geoBean));
//                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
//                if (Code.OK == geoBean.getCode()) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            showCityInfo(geoBean);
//                        }
//                    });
//                }
//            }
//        });
//    }


    /**
     * 查询热门城市
     */
    public void queryCityHot(Context context) {
        QWeather.getGeoTopCity(context, new QWeather.OnResultGeoListener() {
            public static final String TAG = "city_hot_search";

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "on error: ", throwable);
                //System.out.println("City Hot Search Error:"+new Gson());
                Toast.makeText(context, "连接服务器失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                Log.i(TAG, "getHotCity onSuccess: " + new Gson().toJson(geoBean));
                //System.out.println("获取热门城市成功"+new Gson().toJson(geoBean));
                if (Code.OK == geoBean.getCode()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<GeoBean.LocationBean> locationBeanList = geoBean.getLocationBean();
                            for (GeoBean.LocationBean bean : locationBeanList) {
                                CityHot cityHot = new CityHot();
                                cityHot.setCityName(bean.getName());
                                cityHot.setCityId(bean.getId());
                                //更新数据库
                                cityHot.save();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示搜索结果列表
     */
    private void showSearch() {
        searchListView.setVisibility(View.VISIBLE);
        searchTitle.setVisibility(View.GONE);
        hotRecycleView.setVisibility(View.GONE);
    }

    /**
     * 隐藏搜索结果列表
     */
    private void hideSearch() {
        searchListView.setVisibility(View.GONE);
        searchTitle.setVisibility(View.VISIBLE);
        hotRecycleView.setVisibility(View.VISIBLE);
    }

}