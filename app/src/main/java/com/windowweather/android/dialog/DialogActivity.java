package com.windowweather.android.dialog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.windowweather.android.R;
import com.windowweather.android.setting.SysApplication;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        SysApplication.getInstance().addActivity(this);

        Button dialogBack = findViewById(R.id.activity_dialog_back);
        TextView dialogTitle = findViewById(R.id.activity_dialog_textview);
        TextView privacyTitle = findViewById(R.id.privacy_title);
        TextView privacyText = findViewById(R.id.privacy_text);

        Intent intent = getIntent();
        String data = intent.getStringExtra("dialog");
        if (data != null && data.equals("user")) {
            dialogTitle.setText("用户协议");
            privacyTitle.setText("窗口天气用户协议");
            privacyText.setText(R.string.userText);
        }

        dialogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}