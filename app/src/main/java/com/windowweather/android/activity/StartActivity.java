package com.windowweather.android.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.windowweather.android.R;
import com.windowweather.android.db.CitySearch;
import com.windowweather.android.dialog.DialogActivity;
import com.windowweather.android.setting.SysApplication;

import org.litepal.LitePal;

public class StartActivity extends AppCompatActivity {
    private AlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        SysApplication.getInstance().addActivity(this);

        if (LitePal.findFirst(CitySearch.class) == null) {
            loadDialog();
        } else {
            Intent intent=new Intent(StartActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 首次进入应用时加载对话框
     */
    private void loadDialog() {
        View dialogView = View.inflate(StartActivity.this,R.layout.main_dialog,null);

        if(dialog==null) {
            AlertDialog.Builder builder=new AlertDialog.Builder(StartActivity.this);
            dialog=builder.create();
            dialog.show();
            // 获取Window对象
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // 设置显示视图内容
            window.setContentView(dialogView);
        } else {
            dialog.show();
        }

        TextView dialogPrivacy=dialogView.findViewById(R.id.dialog_privacy);
        TextView dialogUser=dialogView.findViewById(R.id.dialog_user);
        Button dialogTrue=dialogView.findViewById(R.id.dialog_true);
        Button dialogFalse=dialogView.findViewById(R.id.dialog_false);

        dialogPrivacy.setLongClickable(false);
        dialogUser.setLongClickable(false);

        //设置监听器
        dialogPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StartActivity.this, DialogActivity.class);
                startActivity(intent);
            }
        });
        dialogUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StartActivity.this, DialogActivity.class);
                intent.putExtra("dialog","user");
                startActivity(intent);
            }
        });
        dialogTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(StartActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialogFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysApplication.getInstance().exit();
            }
        });
    }

}