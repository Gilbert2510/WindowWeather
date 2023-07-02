package com.windowweather.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qweather.sdk.bean.Basic;
import com.qweather.sdk.bean.air.AirDailyBean;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;
import com.windowweather.android.db.City;
import com.windowweather.android.db.WeatherDaily;
import com.windowweather.android.db.WeatherHourly;
import com.windowweather.android.util.Utility;

import org.litepal.LitePal;

import java.util.List;
import java.util.zip.Inflater;

public class WeatherActivity extends AppCompatActivity {
    //初始化各个组件
    //标题栏组件
    private Toolbar weatherToolbar;
    private TextView weatherToolbarTitle;
    //当前预报控件
    private TextView nowTemp;
    private TextView nowTempText;
    private TextView tempBorder;
    private TextView nowFeelsLike;
    //每天预报控件
    private LinearLayout dailyLinearLayout;
    //小时预报控件
    private LinearLayout hourlyLinearLayout;
    /**
     * 城市天气列表
     * 小时天气列表
     * 每日天气列表
     */
    private List<City> cityList;
    private List<WeatherHourly> weatherHourlyList;
    private List<WeatherDaily> weatherDailyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //标题栏透明
        Utility.translucentStatusBar(WeatherActivity.this, true);
        //获取界面组件
        weatherToolbar = findViewById(R.id.weather_toolbar);
        weatherToolbarTitle = findViewById(R.id.weather_toolbar_title);
        nowTemp = findViewById(R.id.weather_main_now_temp);
        nowTempText = findViewById(R.id.weather_main_now_tempText);
        tempBorder = findViewById(R.id.weather_main_now_tempBorder);
        nowFeelsLike = findViewById(R.id.weather_main_now_feelslike);
        dailyLinearLayout=findViewById(R.id.dayforecast_linearlayout);
        hourlyLinearLayout=findViewById(R.id.hourforecast_linearlayout);

        //设置ToolBar
        setSupportActionBar(weatherToolbar);
        weatherToolbar.setNavigationIcon(R.drawable.city_add_vector);
        //ToolBar跳转城市管理
        weatherToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, CityManageActivity.class);
                startActivity(intent);
            }
        });
        //ToolBar菜单栏选项
        weatherToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.option_feedback) {
                    Toast.makeText(WeatherActivity.this, "该功能正在开发中", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.option_sharing) {
                    Toast.makeText(WeatherActivity.this, "该功能正在开发中", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.option_setting) {
                    Toast.makeText(WeatherActivity.this, "该功能正在开发中", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        Intent intent=getIntent();
        String id=intent.getStringExtra("cityId");
        queryWeatherNow(id);
        queryWeather24H(id);
        queryWeather7D(id);

//        cityList=LitePal.where("cityId = ?",id).find(City.class);
//        if(cityList==null) {
//
//        }
//        weatherHourlyList=LitePal.where("cityId = ?",id).find(WeatherHourly.class);
//        if(weatherHourlyList==null) {
//
//        }
//        weatherDailyList=LitePal.where("cityId = ?",id).find(WeatherDaily.class);
//        if(weatherDailyList==null) {
//
//        }


//        //更新天气
//        cityName = "襄城区";
//        weatherToolbarTitle.setText(cityName);


    }

    //设置ToolBar菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_option, menu);
        return true;
    }

    //隐藏ToolBar标题栏
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (weatherToolbar != null) {
            weatherToolbar.setTitle("");
        }
    }

    /**
     * 显示当前天气信息
     */
    @SuppressLint("SetTextI18n")
    public void showWeatherNowInfo(WeatherNowBean weatherNowBean) {
        //当前天气类
        WeatherNowBean.NowBaseBean nowBaseBean = weatherNowBean.getNow();
        Basic basic = weatherNowBean.getBasic();
        /**
         //实况观测时间
         String obsTime=nowBaseBean.getObsTime();
         //体感温度
         String feelsLike=nowBaseBean.getFeelsLike();
         //温度
         String temp=nowBaseBean.getTemp();
         //天气状况
         String text=nowBaseBean.getText();
         //城市的天气预报网页
         String fxLink=basic.getFxLink();
         */
        /**
         * 风向
         * 风力
         * 风速
         * 相对湿度
         * 降水量
         * 大气压强
         * 能见度
         */
        String temp=nowBaseBean.getTemp();
        nowFeelsLike.setText(nowBaseBean.getFeelsLike() + "℃");
        nowTemp.setText(temp);
        nowTempText.setText(nowBaseBean.getText());


    }

    /**
     * 查询当前天气
     */
    public void queryWeatherNow(String location) {
        QWeather.getWeatherNow(WeatherActivity.this, location, new QWeather.OnResultWeatherNowListener() {
            public static final String TAG = "weather_now";

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "on error: ", throwable);
                //System.out.println("Weather Now Error:"+new Gson());
                Looper.prepare();
                Toast.makeText(WeatherActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                Log.i(TAG, "getWeatherNow onSuccess: " + new Gson().toJson(weatherNowBean));
                //System.out.println("获取当前天气成功"+new Gson().toJson(weatherNowBean));
                if (Code.OK == weatherNowBean.getCode()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeatherNowInfo(weatherNowBean);
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示小时天气信息
     */
    @SuppressLint("SetTextI18n")
    public void showWeatherHourlyInfo(WeatherHourlyBean weatherHourlyBean) {
        //小时天气类
        List<WeatherHourlyBean.HourlyBean> dailyBeanList = weatherHourlyBean.getHourly();
        Basic basic = weatherHourlyBean.getBasic();

        for(WeatherHourlyBean.HourlyBean bean:dailyBeanList) {
            View view= LayoutInflater.from(this).inflate(R.layout.weather_hourforecast_item,hourlyLinearLayout,false);
            ImageView hourlyImg=view.findViewById(R.id.weather_hourforecast_item_img);
            TextView hourlyDate=view.findViewById(R.id.weather_hourforecast_item_date);
            TextView hourlyTemp=view.findViewById(R.id.weather_hourforecast_item_temp);
            hourlyImg.setImageResource(R.drawable.w100);
            hourlyDate.setText(bean.getFxTime().substring(12,16));
            hourlyTemp.setText(bean.getTemp()+"℃");
            hourlyLinearLayout.addView(view);
        }

    }

    /**
     * 查询24小时天气
     */
    public void queryWeather24H(String location) {
        QWeather.getWeather24Hourly(WeatherActivity.this, location, new QWeather.OnResultWeatherHourlyListener() {
            public static final String TAG = "weather_hourly";

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "on error: ", throwable);
                //System.out.println("Weather Hourly Error:"+new Gson());
            }

            @Override
            public void onSuccess(WeatherHourlyBean weatherHourlyBean) {
                Log.i(TAG, "getWeatherHourly onSuccess: " + new Gson().toJson(weatherHourlyBean));
                //System.out.println("获取小时天气成功"+new Gson().toJson(weatherNowBean));
                if (Code.OK == weatherHourlyBean.getCode()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeatherHourlyInfo(weatherHourlyBean);
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示每天天气信息
     */
    @SuppressLint("SetTextI18n")
    public void showWeatherDailyInfo(WeatherDailyBean weatherDailyBean) {
        //每天天气类
        List<WeatherDailyBean.DailyBean> dailyBeanList = weatherDailyBean.getDaily();
        Basic basic = weatherDailyBean.getBasic();
        //设置当天最高温和最低温
        tempBorder.setText(dailyBeanList.get(0).getTempMin() + " ~ " + dailyBeanList.get(0).getTempMax() + "℃");
        //读取7天预报依次设置
        dailyLinearLayout.removeAllViews();
        for(WeatherDailyBean.DailyBean bean:dailyBeanList) {
            View view= LayoutInflater.from(this).inflate(R.layout.weather_dayforecast_item,dailyLinearLayout,false);
            TextView dailyDate=view.findViewById(R.id.weather_dayforecast_item_date);
            ImageView dailyImg=view.findViewById(R.id.weather_dayforecast_item_img);
            TextView dailyMax=view.findViewById(R.id.weather_dayforecast_item_max);
            TextView dailyMin=view.findViewById(R.id.weather_dayforecast_item_min);
            dailyDate.setText(bean.getFxDate().substring(5));
            dailyImg.setImageResource(R.drawable.w100);
            dailyMax.setText(bean.getTempMax());
            dailyMin.setText(bean.getTempMin());
            dailyLinearLayout.addView(view);
        }

    }

    /**
     * 查询未来7天天气
     */
    public void queryWeather7D(String location) {
        QWeather.getWeather7D(WeatherActivity.this, location, new QWeather.OnResultWeatherDailyListener() {
            public static final String TAG = "weather_daily";

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "on error: ", throwable);
                //System.out.println("Weather Daily Error:"+new Gson());
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                Log.i(TAG, "getWeatherHourly onSuccess: " + new Gson().toJson(weatherDailyBean));
                //System.out.println("获取每天天气成功"+new Gson().toJson(weatherNowBean));
                if (Code.OK == weatherDailyBean.getCode()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeatherDailyInfo(weatherDailyBean);
                        }
                    });
                }
            }
        });
    }
}