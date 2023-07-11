package com.windowweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.windowweather.android.adapter.CityManageAdapter;
import com.windowweather.android.db.City;

import org.litepal.LitePal;

import java.util.List;

public class CityManageChangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_change);

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
}