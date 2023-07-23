package com.windowweather.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.windowweather.android.R;
import com.windowweather.android.receiver.NetworkChangeReceiver;
import com.windowweather.android.setting.SysApplication;

import org.litepal.LitePal;

public class MainSettingActivity extends AppCompatActivity {
    NetworkChangeReceiver netWorkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);
        SysApplication.getInstance().addActivity(this);

        //注册网络状态监听广播
        netWorkReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkReceiver, filter);

        RelativeLayout mainSettingClear = findViewById(R.id.main_setting_clear);
        RelativeLayout exitActivity=findViewById(R.id.main_setting_exit);

        mainSettingClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LitePal.deleteDatabase("WeatherInfo");
                SysApplication.getInstance().exit();
            }
        });

        exitActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysApplication.getInstance().exit();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netWorkReceiver != null) {
            unregisterReceiver(netWorkReceiver);
        }
    }

}