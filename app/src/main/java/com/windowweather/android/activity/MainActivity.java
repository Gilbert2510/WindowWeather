package com.windowweather.android.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.qweather.sdk.view.HeConfig;
import com.windowweather.android.R;
import com.windowweather.android.db.City;
import com.windowweather.android.db.CitySearch;
import com.windowweather.android.dialog.DialogActivity;
import com.windowweather.android.setting.SysApplication;
import com.windowweather.android.util.CsvUtils;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Intent intent = new Intent(MainActivity.this, CitySearchActivity.class);
            intent.putExtra("main", "none");
            startActivity(intent);
            finish();
        }
    };
    private ProgressBar progressBar;
    private TextView loadView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SysApplication.getInstance().addActivity(this);

        progressBar = findViewById(R.id.main_progress);
        loadView = findViewById(R.id.main_textview);
        progressBar.setVisibility(View.GONE);
        loadView.setVisibility(View.GONE);

        //全局配置SDK账号
        HeConfig.init("HE2306072240111125", "e5234bab755f4ad884fb4bc6582813da");
        //切换至免费订阅
        HeConfig.switchToDevService();

        LottieAnimationView lottieView = findViewById(R.id.main_lottie);
        lottieView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                progressBar.setVisibility(View.VISIBLE);
                loadView.setVisibility(View.VISIBLE);
                if (LitePal.findFirst(CitySearch.class) == null) {
                    isFirst();
                } else {
                    //根据判断数据库中是否有存储选择直接进入天气界面还是城市搜索添加界面
                    if (LitePal.findFirst(City.class) != null) {
                        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(MainActivity.this, CitySearchActivity.class);
                        intent.putExtra("main", "none");
                        startActivity(intent);
                        finish();
                    }
                }

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
            }
        });

    }

    private void isFirst() {
        //第一次进入加载csv文件
        Connector.getDatabase();
        CsvUtils.readDataCsv(MainActivity.this);
        CsvUtils.readCityHotCsv(MainActivity.this);
        Intent intent = new Intent(MainActivity.this, CitySearchActivity.class);
        intent.putExtra("main","none");
        startActivity(intent);
        finish();
    }


}
