package com.windowweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.view.QWeather;
import com.windowweather.android.adapter.HotCityAdapter;
import com.windowweather.android.db.CityHot;
import com.windowweather.android.db.CitySearch;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class CitySearchActivity extends AppCompatActivity {
    private List<CitySearch> searchList;
    private List<CityHot> cityHotList;
    //搜索界面控件
    private ListView searchListView;
    private TextView searchTitle;
    private RecyclerView searchRecycleView;

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
        searchListView=findViewById(R.id.activity_search_listview);
        searchTitle=findViewById(R.id.activity_search_title);
        searchRecycleView=findViewById(R.id.activity_search_recyclerview);

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
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("query", query);

            //todo

            searchList=LitePal.where("cityName = ?",query).find(CitySearch.class);
            if(searchList==null) {
                searchList=new ArrayList<>();
                queryCitySearch(this,query);
            }
            ArrayAdapter<CitySearch> adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,searchList);
            ListView searchListView=findViewById(R.id.activity_search_listview);
            searchListView.setAdapter(adapter);
            showSearch();
            /**
             * 设置搜索城市列表的监听器
             * 当点击时返回管理界面
             */
            searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CitySearch search= adapter.getItem(position);
                    Intent intent=new Intent(view.getContext(), CityManageActivity.class);
                    intent.putExtra("searchCityId",search.getCityId());
                    startActivity(intent);
                    finish();
                }
            });
        }

        /**
         * 显示热门城市列表
         * 当查询数据库不为空是直接查询热门城市数据库并显示，否则显示查询服务器返回的数据
         */
        cityHotList=LitePal.findAll(CityHot.class);
        if(cityHotList==null) {
            queryCityHot(this);
            cityHotList=new ArrayList<>();
        }
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3,LinearLayoutManager.VERTICAL,false);
        HotCityAdapter hotAdapter=new HotCityAdapter(cityHotList);
        searchRecycleView.setLayoutManager(gridLayoutManager);
        searchRecycleView.setAdapter(hotAdapter);
        /**
         * 热门城市列表的监听器
         * 当点击时返回管理界面
         */
        hotAdapter.setOnItemClickListener(new HotCityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String id=cityHotList.get(position).getCityId();
                Intent intent=new Intent(view.getContext(), CityManageActivity.class);
                intent.putExtra("searchCityId",id);
                startActivity(intent);
                finish();
            }
        });

    }

    /**
     * 连接服务器时显示对话框
     */
    private void showWaitingDialog() {
        //toDo
    }

    /**
     * 显示查询城市的结果
     */
    public void showCityInfo(GeoBean geoBean) {
        List<GeoBean.LocationBean> locationBeanList=geoBean.getLocationBean();
        CitySearch city=new CitySearch();
        //第一次搜索服务器的数据存入数据库
        for(GeoBean.LocationBean bean:locationBeanList) {
            String name=bean.getName();
            String adm2=bean.getAdm2();
            String adm1=bean.getAdm1();
            String country=bean.getCountry();
            String areaName=name+"-"+adm2+"-"+adm1+"-"+country;
            city.setCityId(bean.getId());
            city.setCityName(name);
            city.setCityAdm2(adm2);
            city.setCityAdm1(adm1);
            city.setCityCountry(country);
            city.setAreaName(areaName);
            searchList.add(city);
            city.save();
        }
    }

    /**
     * 查询城市
     */
    public void queryCitySearch(Context context, String location) {
        QWeather.getGeoCityLookup(context, location, new QWeather.OnResultGeoListener() {
            public static final String TAG = "city_search";

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "on error: ", throwable);
                //System.out.println("City Search Error:"+new Gson());
                Toast.makeText(context, "连接服务器失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                Log.i(TAG, "getCity onSuccess: " + new Gson().toJson(geoBean));
                //System.out.println("获取城市成功"+new Gson().toJson(geoBean));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK == geoBean.getCode()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showCityInfo(geoBean);
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示热门城市列表
     */
    public void showHotCityInfo(GeoBean geoBean) {
        List<GeoBean.LocationBean> locationBeanList=geoBean.getLocationBean();
        CityHot cityHot=new CityHot();
        for(GeoBean.LocationBean bean:locationBeanList) {
            cityHot.setCityName(bean.getName());
            cityHot.setCityId(bean.getId());
            //更新数据库
            cityHot.save();
            cityHotList.add(cityHot);
        }
    }

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
                            showHotCityInfo(geoBean);
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
        searchRecycleView.setVisibility(View.GONE);
    }

    /**
     * 隐藏搜索结果列表
     */
    private void hideSearch() {
        searchListView.setVisibility(View.GONE);
        searchTitle.setVisibility(View.VISIBLE);
        searchRecycleView.setVisibility(View.VISIBLE);
    }

}