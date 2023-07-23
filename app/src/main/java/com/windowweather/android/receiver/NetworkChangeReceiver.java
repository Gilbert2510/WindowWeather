package com.windowweather.android.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * 网络广播接收器
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private static long WIFI_TIME = 0;
    private static long ETHERNET_TIME = 0;
    private static long NONE_TIME = 0;
    private static int LAST_TYPE = -3;
    private static String TAG = "TAG";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        // 特殊注意：如果if条件生效，那么证明当前是有连接wifi或移动网络的，如果有业务逻辑最好把esle场景酌情考虑进去！
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            long time = getTime();
            if (time != WIFI_TIME && time != ETHERNET_TIME && time != NONE_TIME) {
                final int netWorkState = getNetWorkState(context);
                if (netWorkState == 0 && LAST_TYPE != 0) {
                    WIFI_TIME = time;
                    LAST_TYPE = netWorkState;
                    Toast.makeText(context,"当前处于WIFI连接",Toast.LENGTH_SHORT).show();
                } else if (netWorkState == 1 && LAST_TYPE != 1) {
                    ETHERNET_TIME = time;
                    LAST_TYPE = netWorkState;
                    Toast.makeText(context,"当前处于移动网络连接",Toast.LENGTH_SHORT).show();

                } else if (netWorkState == -1 && LAST_TYPE != -1) {
                    NONE_TIME = time;
                    LAST_TYPE = netWorkState;
                    Toast.makeText(context,"当前无网络连接",Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    public long getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String date = sDateFormat.format(new java.util.Date());
        return Long.parseLong(date);
    }

    private static final int NETWORK_NONE = -1; //无网络连接
    private static final int NETWORK_WIFI = 0; //wifi
    private static final int NETWORK_MOBILE = 1; //数据网络
    //判断网络状态与类型
    public static int getNetWorkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }
}
