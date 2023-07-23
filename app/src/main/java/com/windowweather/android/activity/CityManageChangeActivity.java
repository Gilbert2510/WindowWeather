package com.windowweather.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.windowweather.android.R;
import com.windowweather.android.adapter.CityManageAdapter;
import com.windowweather.android.db.City;
import com.windowweather.android.receiver.NetworkChangeReceiver;
import com.windowweather.android.setting.SysApplication;

import org.litepal.LitePal;

import java.util.List;

public class CityManageChangeActivity extends AppCompatActivity {
    NetworkChangeReceiver netWorkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_change);
        SysApplication.getInstance().addActivity(this);

        //注册网络状态监听广播
        netWorkReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkReceiver, filter);

        Button selectAllButton = findViewById(R.id.manage_change_selectall);
        Button cancelButton = findViewById(R.id.manage_change_cancel);
        RecyclerView manageRecyclerView=findViewById(R.id.manage_change_recyclerview);
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CityManageChangeActivity.this,"正在开发",Toast.LENGTH_SHORT).show();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        List<City> cityList = LitePal.findAll(City.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        manageRecyclerView.setLayoutManager(layoutManager);
        CityManageAdapter cityManageAdapter = new CityManageAdapter(cityList,true);
        manageRecyclerView.setAdapter(cityManageAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netWorkReceiver != null) {
            unregisterReceiver(netWorkReceiver);
        }
    }
}