package com.mho.web.wv.mohong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.mho.web.wv.mohong.manager.PermissionManager;
import com.mho.web.wv.mohong.ui.GestureGuideActivity;
import com.mho.web.wv.mohong.utils.FileManager;

import java.util.Random;

public class SplashActivity extends Activity {

    private WebView webView;
    private Handler handler = new Handler();
    private int playCount = 0;
    private int targetCount;
    private boolean skipped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 请求权限
        PermissionManager.requestAllPermissions(this);

        // 创建全屏布局
        FrameLayout layout = new FrameLayout(this);

        // 创建WebView用于播放SVG动画
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 页面加载完成，开始播放动画
                startAnimationLoop();
            }
        });

        // 加载SVG动画
        FileManager fileManager = FileManager.getInstance(this);
        String svgPath = fileManager.getSplashSvgPath();
        webView.loadUrl(svgPath);

        layout.addView(webView);

        // 添加跳过按钮
        android.widget.Button skipButton = new android.widget.Button(this);
        skipButton.setText("跳过");
        skipButton.setTextSize(12);
        skipButton.setBackgroundColor(0x88000000);
        skipButton.setTextColor(0xFFFFFFFF);
        android.widget.FrameLayout.LayoutParams btnParams = new android.widget.FrameLayout.LayoutParams(
        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT);
        btnParams.gravity = android.view.Gravity.TOP | android.view.Gravity.END;
        btnParams.setMargins(0, 48, 32, 0);
        skipButton.setLayoutParams(btnParams);
        skipButton.setOnClickListener(v -> {
            skipped = true;
            finishSplash();
        });
        layout.addView(skipButton);

        setContentView(layout);

        // 随机播放次数 1-3 次
        targetCount = new Random().nextInt(3) + 1;
    }

    private void startAnimationLoop() {
        if (skipped) return;

        // 延迟2秒模拟一次动画播放
        handler.postDelayed(() -> {
            playCount++;
            if (playCount >= targetCount) {
                finishSplash();
            } else {
                // 重新加载动画
                FileManager fileManager = FileManager.getInstance(this);
                webView.loadUrl(fileManager.getSplashSvgPath());
            }
        }, 2000);
    }

    private void finishSplash() {
        handler.removeCallbacksAndMessages(null);

        Intent intent;
        if (GestureGuideActivity.isFirstLaunch(this)) {
            intent = new Intent(this, GestureGuideActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }
}