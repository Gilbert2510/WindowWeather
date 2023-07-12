package com.windowweather.android;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qweather.sdk.bean.Basic;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;
import com.windowweather.android.db.City;
import com.windowweather.android.db.WeatherDaily;
import com.windowweather.android.db.WeatherHourly;
import com.windowweather.android.fragment.WeatherSunView;
import com.windowweather.android.util.BarUtils;
import com.windowweather.android.util.ResourceUtils;

import org.litepal.LitePal;

import java.util.List;

public class WeatherActivity extends AppCompatActivity {
    /**
     * 初始化各个组件
     * 标题栏组件
     * 当前预报控件
     * 每天预报控件
     * 小时预报控件
     */
    private Toolbar weatherToolbar;
    private TextView weatherToolbarTitle;
    private TextView nowTemp;
    private TextView nowTempText;
    private TextView tempBorder;
    private TextView nowFeelsLike;
    private LinearLayout dailyLinearLayout;
    private LinearLayout hourlyLinearLayout;
    private LinearLayout weatherLinearLayout;
    private String cityId;
    private String cityName;
    private TextView feels;
    private TextView wind;
    private TextView windText;
    private TextView humidity;
    private TextView vis;
    private TextView uv;
    private TextView pressure;
    private WeatherSunView sunView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //标题栏透明
        BarUtils.translucentStatusBar(WeatherActivity.this, true);
        //获取界面组件
        SwipeRefreshLayout refreshLayout = findViewById(R.id.activity_weather_refresh);
        weatherToolbar = findViewById(R.id.weather_toolbar);
        weatherToolbarTitle = findViewById(R.id.weather_toolbar_title);
        nowTemp = findViewById(R.id.weather_main_now_temp);
        nowTempText = findViewById(R.id.weather_main_now_tempText);
        tempBorder = findViewById(R.id.weather_main_now_tempBorder);
        nowFeelsLike = findViewById(R.id.weather_main_now_feelslike);
        dailyLinearLayout = findViewById(R.id.dayforecast_linearlayout);
        hourlyLinearLayout = findViewById(R.id.hourforecast_linearlayout);
        weatherLinearLayout = findViewById(R.id.activity_weather_linearlayout);

        feels=findViewById(R.id.weather_other_feels);
        wind=findViewById(R.id.weather_other_wind);
        windText=findViewById(R.id.weather_other_windText);
        humidity=findViewById(R.id.weather_other_humidity);
        vis=findViewById(R.id.weather_other_vis);
        uv=findViewById(R.id.weather_other_uv);
        pressure=findViewById(R.id.weather_other_pressure);
        sunView=findViewById(R.id.weather_sun_view);

        //设置ToolBar
        setSupportActionBar(weatherToolbar);
        weatherToolbar.setNavigationIcon(R.drawable.city_add_vector);

        /**
         * 刷新监听器
         */
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    showNowInfo(cityId, cityName);
                    queryWeather24H(WeatherActivity.this, cityId, cityName);
                    List<WeatherDaily> dailyList = LitePal.where("cityId = ?", cityId).find(WeatherDaily.class);
                    showOtherInfo(cityId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "更新天气成功", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        /**
         * 读取从城市管理返回的结果，若为RESULT_OK则展示该城市页面
         */
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intent = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode == RESULT_OK) {
                    assert intent != null;
                    cityId = intent.getStringExtra("cityId");
                    cityName = intent.getStringExtra("cityName");
                    //在管理界面选择了城市
                    showNowInfo(cityId, cityName);
                    List<WeatherDaily> dailyList = LitePal.where("cityId = ?", cityId).find(WeatherDaily.class);
                    if (dailyList.size() > 0) {
                        //此时数据库存有数据，直接查询
                        showWeatherDailyInfo(dailyList);
                    } else {
                        //否则查询服务器
                        queryWeather7D(WeatherActivity.this, cityId, cityName);
                    }
                    queryWeather24H(WeatherActivity.this, cityId, cityName);
                    showOtherInfo(cityId);
                }
            }
        });

        /**
         * 跳转城市管理
         * 若是城市管理返回则带回id和name数据
         */
        weatherToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, CityManageActivity.class);
                launcher.launch(intent);
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

        City city = LitePal.findFirst(City.class);
        if (city != null) {
            cityId = city.getCityId();
            cityName = city.getCityName();
            showNowInfo(cityId, cityName);
            queryWeather24H(WeatherActivity.this, cityId, cityName);
            queryWeather7D(WeatherActivity.this, cityId, cityName);
            List<WeatherDaily> dailyList = LitePal.where("cityId = ?", cityId).find(WeatherDaily.class);
            showOtherInfo(cityId);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * @param menu The options menu in which you place your items.
     * @return
     */
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

    private void showNowInfo(String cityId, String cityName) {
        //设置now的天气
        QWeather.getWeatherNow(WeatherActivity.this, cityId, new QWeather.OnResultWeatherNowListener() {
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
                    WeatherNowBean.NowBaseBean bean = weatherNowBean.getNow();
                    Basic basic = weatherNowBean.getBasic();
                    int id = 0;
                    try {
                        id = (LitePal.select("id").where("cityId = ?", cityId).find(City.class)).get(0).getId();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    City city;
                    //查询城市表，如果存在即flag为true，只更新天气信息
                    city = LitePal.find(City.class, id);
                    city.setObsTime(bean.getObsTime());
                    city.setFeelsLike(bean.getFeelsLike());
                    city.setNowTemp(bean.getTemp());
                    city.setNowText(bean.getText());
                    city.setNowIcon(bean.getIcon());
                    city.save();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nowFeelsLike.setText(city.getFeelsLike() + "℃");
                            nowTemp.setText(city.getNowTemp());
                            nowTempText.setText(city.getNowText());
                            weatherToolbarTitle.setText(cityName);
                            int currentHour = Integer.parseInt(city.getObsTime().substring(11, 13));
                            if (currentHour >= 5 && currentHour < 19) {
                                //此时是白天，设置白天天气壁纸
                                weatherLinearLayout.setBackgroundResource(R.drawable.main_day);
                                //ChangeColorUtils.ChangeColorDay(scrollView,weatherLinearLayout);
                            } else {
                                //此时是夜晚，设置夜晚天气壁纸
                                weatherLinearLayout.setBackgroundResource(R.drawable.main_night);
                                //ChangeColorUtils.ChangeColorNight(scrollView,weatherLinearLayout);
                            }
                        }
                    });
                }
            }
        });
//        Utility.queryWeatherNow(WeatherActivity.this, cityId, cityName, true);
    }

    /**
     * 显示小时天气信息
     */
    @SuppressLint("SetTextI18n")
    public void showWeatherHourlyInfo(List<WeatherHourly> hourlyList) {
        //小时天气类

        for (WeatherHourly hourly : hourlyList) {
            View view = LayoutInflater.from(this).inflate(R.layout.weather_hourforecast_item, hourlyLinearLayout, false);
            ImageView hourlyImg = view.findViewById(R.id.weather_hourforecast_item_img);
            TextView hourlyDate = view.findViewById(R.id.weather_hourforecast_item_date);
            TextView hourlyTemp = view.findViewById(R.id.weather_hourforecast_item_temp);
            int id = ResourceUtils.getDrawableId(WeatherActivity.this, "w" + hourly.getHourlyIcon());
            hourlyImg.setImageResource(id);
            hourlyDate.setText(hourly.getHourlyFxTime().substring(11, 16));
            hourlyTemp.setText(hourly.getHourlyTemp() + "℃");
            City city=(LitePal.where("cityId = ?",hourly.getCityId()).find(City.class)).get(0);
            int currentHour = Integer.parseInt(city.getObsTime().substring(11, 13));
//            if (currentHour >= 5 && currentHour < 19) {
//                //此时是白天
//                hourlyDate.setTextColor(Color.BLACK);
//                hourlyTemp.setTextColor(Color.BLACK);
//            } else {
                //此时是夜晚
                hourlyDate.setTextColor(Color.WHITE);
                hourlyTemp.setTextColor(Color.WHITE);
//            }
            hourlyLinearLayout.addView(view);

        }

    }

    /**
     * 查询24小时天气
     */
    public void queryWeather24H(Context context, String location, String cityName) {
        QWeather.getWeather24Hourly(context, location, new QWeather.OnResultWeatherHourlyListener() {
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
                    List<WeatherHourlyBean.HourlyBean> beanList = weatherHourlyBean.getHourly();
                    List<WeatherHourly> hourlyList = LitePal.findAll(WeatherHourly.class);
                    if (hourlyList.size() != 0) {
                        //不为空时删除
                        LitePal.deleteAll(WeatherHourly.class);
                    }
                    for (WeatherHourlyBean.HourlyBean bean : beanList) {
                        WeatherHourly hourly = new WeatherHourly();
                        hourly.setCityId(location);
                        hourly.setCityName(cityName);
                        hourly.setHourlyFxTime(bean.getFxTime());
                        hourly.setHourlyTemp(bean.getTemp());
                        hourly.setHourlyText(bean.getText());
                        hourly.setHourlyPop(bean.getPop());
                        hourly.setHourlyPrecip(bean.getPrecip());
                        hourly.setHourlyIcon(bean.getIcon());
                        hourly.setHourlyPressure(bean.getPressure());
                        hourly.save();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<WeatherHourly> hourlyList = LitePal.where("cityId = ?", location).find(WeatherHourly.class);
                            showWeatherHourlyInfo(hourlyList);
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
    public void showWeatherDailyInfo(List<WeatherDaily> dailyList) {
        WeatherDaily day=dailyList.get(0);
        //设置当天最高温和最低温
        tempBorder.setText(day.getDailyMin() + " ~ " + day.getDailyMax() + "℃");
        //读取7天预报依次设置
        dailyLinearLayout.removeAllViews();
        //设置其他数据
        String uvIndex=day.getDailyUvIndex();
        String strUv;
        if(uvIndex.equals("1")) {
            strUv="最弱";
        } else if(uvIndex.equals("2")) {
            strUv="弱";
        } else if(uvIndex.equals("3")) {
            strUv="中等";
        } else if(uvIndex.equals("4")) {
            strUv="强";
        } else {
            strUv="很强";
        }
        uv.setText(strUv);
        //设置日出日落
        String sunRise=day.getDailySunRise();
        String sunSet=day.getDailySunSet();
        sunView.setSunriseTime(sunRise);
        sunView.setSunsetTime(sunSet);
        for (WeatherDaily daily : dailyList) {
            View view = LayoutInflater.from(this).inflate(R.layout.weather_dayforecast_item, dailyLinearLayout, false);
            TextView dailyDate = view.findViewById(R.id.weather_dayforecast_item_date);
            ImageView dailyImg = view.findViewById(R.id.weather_dayforecast_item_img);
            TextView dailyMax = view.findViewById(R.id.weather_dayforecast_item_max);
            TextView dailyMin = view.findViewById(R.id.weather_dayforecast_item_min);
            dailyDate.setText(daily.getDailyFxDate().substring(5));
            dailyMax.setText(daily.getDailyMax());
            dailyMin.setText(daily.getDailyMin());
            int id = ResourceUtils.getDrawableId(WeatherActivity.this, "w" + daily.getDailyIconDay());
            dailyImg.setImageResource(id);
//            City city=(LitePal.where("cityId = ?",daily.getCityId()).find(City.class)).get(0);
//            int currentHour = Integer.parseInt(city.getObsTime().substring(11, 13));
//            if (currentHour >= 5 && currentHour < 19) {
//                //此时是白天
//                dailyDate.setTextColor(Color.BLACK);
//                dailyMax.setTextColor(Color.BLACK);
//                dailyMin.setTextColor(Color.BLACK);
//            } else {
                //此时是夜晚
                dailyDate.setTextColor(Color.WHITE);
                dailyMax.setTextColor(Color.WHITE);
                dailyMin.setTextColor(Color.WHITE);
//            }
            dailyLinearLayout.addView(view);
        }

    }

    /**
     * 查询未来7天天气
     */
    public void queryWeather7D(Context context, String location, String cityName) {
        QWeather.getWeather7D(context, location, new QWeather.OnResultWeatherDailyListener() {
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
                    List<WeatherDailyBean.DailyBean> dailyBeanList = weatherDailyBean.getDaily();
                    List<WeatherDaily> dailyList = LitePal.findAll(WeatherDaily.class);
                    if (dailyList.size() != 0) {
                        LitePal.deleteAll(WeatherDaily.class);
                    }
                    for (WeatherDailyBean.DailyBean bean : dailyBeanList) {
                        WeatherDaily daily = new WeatherDaily();
                        daily.setCityId(location);
                        daily.setCityName(cityName);
                        daily.setDailyFxDate(bean.getFxDate());
                        daily.setDailySunRise(bean.getSunrise());
                        daily.setDailySunSet(bean.getSunset());
                        daily.setDailyMoonRise(bean.getMoonRise());
                        daily.setDailyMoonSet(bean.getMoonSet());
                        daily.setDailyMoonPhase(bean.getMoonPhase());
                        daily.setDailyMax(bean.getTempMax());
                        daily.setDailyMin(bean.getTempMin());
                        daily.setDailyIconDay(bean.getIconDay());
                        daily.setDailyTextDay(bean.getTextDay());
                        daily.setDailyTextNight(bean.getTextNight());
                        daily.setDailyUvIndex(bean.getUvIndex());
                        daily.save();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<WeatherDaily> dailyList = LitePal.where("cityId = ?", location).find(WeatherDaily.class);
                            showWeatherDailyInfo(dailyList);
                        }
                    });
                }
            }
        });
    }

    private void showOtherInfo(String cityId) {
        City city=(LitePal.where("cityId = ?",cityId).find(City.class)).get(0);
        feels.setText(city.getFeelsLike()+"℃");
        wind.setText(city.getNowWindScale()+"级");
        windText.setText(city.getNowWindDir());
        humidity.setText(city.getNowHumidity()+"%");
        vis.setText(city.getNowVis()+"Km");

        pressure.setText(city.getNowPressure()+" hPa");
    }
}