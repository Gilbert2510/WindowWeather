package com.windowweather.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.windowweather.android.R;
import com.windowweather.android.db.City;
import com.windowweather.android.db.CitySearch;
import com.windowweather.android.setting.SysApplication;
import com.windowweather.android.util.CsvUtils;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

public class FirstLoadActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView textView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                isFirst();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });

        setContentView(R.layout.activity_first_load);
        SysApplication.getInstance().addActivity(this);

        progressBar = findViewById(R.id.first_load_progress);
        textView = findViewById(R.id.first_load_textview);


    }

    /**
     * 判断是否是第一次进入程序
     */
    private void isFirst() {
        if (LitePal.findFirst(CitySearch.class) == null) {
            //第一次进入加载csv文件
            Connector.getDatabase();
            CsvUtils.readDataCsv(FirstLoadActivity.this);
            CsvUtils.readCityHotCsv(FirstLoadActivity.this);
            Intent intent = new Intent(FirstLoadActivity.this, CitySearchActivity.class);
            intent.putExtra("main","none");
            startActivity(intent);
            finish();
        } else {
            //根据判断数据库中是否有存储选择直接进入天气界面还是城市搜索添加界面
            if (LitePal.findFirst(City.class) != null) {
                Intent intent = new Intent(FirstLoadActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(FirstLoadActivity.this, CitySearchActivity.class);
                intent.putExtra("main","none");
                startActivity(intent);
                finish();
            }
        }
    }

}