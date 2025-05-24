package com.example.cameraapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private TextView tvSplashTitle; // 开屏页标题
    private static final int SPLASH_DELAY = 2000; // 延迟2秒

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // 加载开屏页布局

        // 初始化组件
        tvSplashTitle = findViewById(R.id.tv_splash_title);
        tvSplashTitle.setText("墨镜相机"); // 设置标题文本

        // 延迟跳转主页面
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // 关闭开屏页，避免返回时重复显示
        }, SPLASH_DELAY);
    }
}
