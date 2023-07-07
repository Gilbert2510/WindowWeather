package com.windowweather.android.util;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.view.ViewCompat;

public class BarUtils {
    /**
     * 透明化系统状态栏
     * @param activity
     * @param hideStatusBarBackground
     */
    public static void translucentStatusBar(Activity activity, boolean hideStatusBarBackground) {      //状态栏和背景融合
        Window window = activity.getWindow();
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (hideStatusBarBackground) {
            //如果为全透明模式，取消设置Window半透明的Flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置状态栏为透明
            window.setStatusBarColor(Color.TRANSPARENT);
            //设置window的状态栏不可见
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            //如果为半透明模式，添加设置Window半透明的Flag
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        //view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

//    /**
//     * 查询当前天气
//     */
//    public static void queryWeatherNow(Context context, String location, String cityName, boolean flag) {
//        QWeather.getWeatherNow(context, location, new QWeather.OnResultWeatherNowListener() {
//            public static final String TAG = "weather_now";
//
//            @Override
//            public void onError(Throwable throwable) {
//                Log.i(TAG, "on error: ", throwable);
//                //System.out.println("Weather Now Error:"+new Gson());
//                Looper.prepare();
//                Toast.makeText(context, "连接服务器失败", Toast.LENGTH_SHORT).show();
//                Looper.loop();
//            }
//
//            @Override
//            public void onSuccess(WeatherNowBean weatherNowBean) {
//                Log.i(TAG, "getWeatherNow onSuccess: " + new Gson().toJson(weatherNowBean));
//                //System.out.println("获取当前天气成功"+new Gson().toJson(weatherNowBean));
//                if (Code.OK == weatherNowBean.getCode()) {
//                    WeatherNowBean.NowBaseBean bean = weatherNowBean.getNow();
//                    Basic basic = weatherNowBean.getBasic();
//                    int id = 0;
//                    try {
//                        id = (LitePal.select("id").where("cityId = ?", location).find(City.class)).get(0).getId();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    City city;
//                    if (flag) {
//                        //查询城市表，如果存在即flag为true，只更新天气信息
//                        city = LitePal.find(City.class, id);
//                    } else {
//                        //否则添加新的城市项
//                        city = new City();
//                        city.setCityName(cityName);
//                        city.setCityId(location);
//                        city.setFxLink(basic.getFxLink());
//                    }
//                    city.setObsTime(bean.getObsTime());
//                    city.setFeelsLike(bean.getFeelsLike());
//                    city.setNowTemp(bean.getTemp());
//                    city.setNowText(bean.getText());
//                    city.setNowIcon(bean.getIcon());
//                    city.save();
//                }
//            }
//        });
//    }

}
