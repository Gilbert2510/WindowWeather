package com.windowweather.android;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
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
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;
import com.windowweather.android.db.City;
import com.windowweather.android.util.BarUtils;
import com.windowweather.android.util.CurrentDateUtils;
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
    private int currentHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //标题栏透明
        BarUtils.translucentStatusBar(WeatherActivity.this, true);
        //获取界面组件
        weatherToolbar = findViewById(R.id.weather_toolbar);
        weatherToolbarTitle = findViewById(R.id.weather_toolbar_title);
        nowTemp = findViewById(R.id.weather_main_now_temp);
        nowTempText = findViewById(R.id.weather_main_now_tempText);
        tempBorder = findViewById(R.id.weather_main_now_tempBorder);
        nowFeelsLike = findViewById(R.id.weather_main_now_feelslike);
        dailyLinearLayout = findViewById(R.id.dayforecast_linearlayout);
        hourlyLinearLayout = findViewById(R.id.hourforecast_linearlayout);
        weatherLinearLayout = findViewById(R.id.activity_weather_linearlayout);

        //设置ToolBar
        setSupportActionBar(weatherToolbar);
        weatherToolbar.setNavigationIcon(R.drawable.city_add_vector);

        /**
         * 读取从城市管理返回的结果，若为RESULT_OK则展示该城市页面
         */
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intent = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode == RESULT_OK) {
                    String cityId = intent.getStringExtra("cityId");
                    String cityName = intent.getStringExtra("cityName");
                    //在管理界面选择了城市
                    showNowInfo(cityId, cityName);
                    queryWeather24H(cityId);
                    queryWeather7D(cityId);
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
            String cityId = city.getCityId();
            String cityName = city.getCityName();
            showNowInfo(cityId, cityName);
            queryWeather24H(cityId);
            queryWeather7D(cityId);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentHour >= 5 && currentHour < 18) {
            //此时是白天，设置白天天气壁纸
            weatherLinearLayout.setBackgroundResource(R.drawable.main_100);
        } else {
            //此时是夜晚，设置夜晚天气壁纸
            weatherLinearLayout.setBackgroundResource(R.color.JackieBlue);
        }
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
                            currentHour = Integer.parseInt(city.getObsTime().substring(11, 13));
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
    public void showWeatherHourlyInfo(WeatherHourlyBean weatherHourlyBean) {
        //小时天气类
        List<WeatherHourlyBean.HourlyBean> dailyBeanList = weatherHourlyBean.getHourly();

        for (WeatherHourlyBean.HourlyBean bean : dailyBeanList) {
            View view = LayoutInflater.from(this).inflate(R.layout.weather_hourforecast_item, hourlyLinearLayout, false);
            ImageView hourlyImg = view.findViewById(R.id.weather_hourforecast_item_img);
            TextView hourlyDate = view.findViewById(R.id.weather_hourforecast_item_date);
            TextView hourlyTemp = view.findViewById(R.id.weather_hourforecast_item_temp);
            int id = ResourceUtils.getDrawableId(WeatherActivity.this, "w" + bean.getIcon());
            hourlyImg.setImageResource(id);
            hourlyDate.setText(bean.getFxTime().substring(11, 16));
            hourlyTemp.setText(bean.getTemp() + "℃");
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
        for (WeatherDailyBean.DailyBean bean : dailyBeanList) {
            View view = LayoutInflater.from(this).inflate(R.layout.weather_dayforecast_item, dailyLinearLayout, false);
            TextView dailyDate = view.findViewById(R.id.weather_dayforecast_item_date);
            ImageView dailyImg = view.findViewById(R.id.weather_dayforecast_item_img);
            TextView dailyMax = view.findViewById(R.id.weather_dayforecast_item_max);
            TextView dailyMin = view.findViewById(R.id.weather_dayforecast_item_min);
            dailyDate.setText(bean.getFxDate().substring(5));
            dailyMax.setText(bean.getTempMax());
            dailyMin.setText(bean.getTempMin());
//            int currentHour = CurrentDateUtils.getCurrentHour();
//            int id;
//            if (currentHour >= 5 && currentHour < 19) {
//                //此时是白天，读取白天的天气状态
//                id = ResourceUtils.getDrawableId(WeatherActivity.this, "w" + bean.getIconDay());
//            } else {
//                //此时是夜晚，读取夜晚的天气状态
//                id = ResourceUtils.getDrawableId(WeatherActivity.this, "w" + bean.getIconNight());
//            }
            int id = ResourceUtils.getDrawableId(WeatherActivity.this, "w" + bean.getIconDay());
            dailyImg.setImageResource(id);
            Log.d("hour", bean.getIconDay());
            Log.d("hour", bean.getIconNight());

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