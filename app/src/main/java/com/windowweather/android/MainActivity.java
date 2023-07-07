package com.windowweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.qweather.sdk.view.HeConfig;
import com.windowweather.android.db.City;
import com.windowweather.android.db.CitySearch;
import com.windowweather.android.util.CsvUtils;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //全局配置SDK账号
        HeConfig.init("HE2306072240111125", "e5234bab755f4ad884fb4bc6582813da");
        //切换至免费订阅
        HeConfig.switchToDevService();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.activity_main_progress);
        textView = findViewById(R.id.activity_main_textview);
        isFirst();
    }

    /**
     * 判断是否是第一次进入程序
     */
    private void isFirst() {
        SharedPreferences shared = getSharedPreferences("is", MODE_PRIVATE);
        boolean isfer = shared.getBoolean("isfer", true);
        SharedPreferences.Editor editor = shared.edit();
        if (LitePal.findFirst(CitySearch.class) == null) {
            //第一次进入加载csv文件
            progressBar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            Connector.getDatabase();
            CsvUtils.readDataCsv(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, CitySearchActivity.class);
            intent.putExtra("main","none");
            startActivity(intent);
            finish();
        } else {
            //根据判断数据库中是否有存储选择直接进入天气界面还是城市搜索添加界面
            if (LitePal.findFirst(City.class) != null) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MainActivity.this, CitySearchActivity.class);
                intent.putExtra("main","none");
                startActivity(intent);
                finish();
            }
        }
    }
}
