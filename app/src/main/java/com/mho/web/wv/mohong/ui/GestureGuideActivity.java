package com.mho.web.wv.mohong.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GestureGuideActivity extends AppCompatActivity {

    private static final String PREF_NAME = "mho_web_prefs";
    private static final String KEY_FIRST_LAUNCH = "first_launch";

    private int currentPage = 0;
    private LinearLayout dotsContainer;
    private TextView gestureText;
    private ImageView gestureImage;
    private Button skipButton;
    private Button nextButton;

    private String[] gestures = {
            "左侧边缘向右滑动\n返回上一页",
            "右侧边缘向左滑动\n前进下一页",
            "双指下滑\n打开功能菜单",
            "双指上滑\n打开标签管理"
    };

    private int[] gestureImages = {
            android.R.drawable.ic_media_previous,
            android.R.drawable.ic_media_next,
            android.R.drawable.ic_menu_view,
            android.R.drawable.ic_menu_agenda
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 64, 32, 64);
        layout.setBackgroundColor(0xFF1E1E1E);

        TextView title = new TextView(this);
        title.setText("手势引导");
        title.setTextSize(24);
        title.setTextColor(0xFFFFFFFF);
        title.setGravity(android.view.Gravity.CENTER);
        title.setPadding(0, 0, 0, 48);
        layout.addView(title);

        gestureImage = new ImageView(this);
        gestureImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        gestureImage.setPadding(0, 0, 0, 48);
        gestureImage.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        300));
        layout.addView(gestureImage);

        gestureText = new TextView(this);
        gestureText.setTextSize(18);
        gestureText.setTextColor(0xFFFFFFFF);
        gestureText.setGravity(android.view.Gravity.CENTER);
        gestureText.setPadding(0, 0, 0, 48);
        layout.addView(gestureText);

        dotsContainer = new LinearLayout(this);
        dotsContainer.setOrientation(LinearLayout.HORIZONTAL);
        dotsContainer.setGravity(android.view.Gravity.CENTER);
        dotsContainer.setPadding(0, 0, 0, 48);
        layout.addView(dotsContainer);

        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setPadding(0, 0, 0, 0);

        skipButton = new Button(this);
        skipButton.setText("跳过");
        skipButton.setOnClickListener(v -> finishGuide());

        nextButton = new Button(this);
        nextButton.setText("下一步");
        nextButton.setOnClickListener(v -> {
            if (currentPage < gestures.length - 1) {
                currentPage++;
                updatePage();
            } else {
                finishGuide();
            }
        });

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        buttonParams.setMargins(16, 0, 16, 0);
        skipButton.setLayoutParams(buttonParams);
        nextButton.setLayoutParams(buttonParams);

        buttonRow.addView(skipButton);
        buttonRow.addView(nextButton);
        layout.addView(buttonRow);

        setContentView(layout);

        updatePage();
    }

    private void updatePage() {
        gestureText.setText(gestures[currentPage]);
        gestureImage.setImageResource(gestureImages[currentPage]);

        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        gestureText.startAnimation(fadeIn);
        gestureImage.startAnimation(fadeIn);

        updateDots();

        if (currentPage == gestures.length - 1) {
            nextButton.setText("完成");
        } else {
            nextButton.setText("下一步");
        }
    }

    private void updateDots() {
        dotsContainer.removeAllViews();
        for (int i = 0; i < gestures.length; i++) {
            View dot = new View(this);
            int size = 16;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(android.R.drawable.presence_online);
            if (i == currentPage) {
                dot.setAlpha(1.0f);
            } else {
                dot.setAlpha(0.3f);
            }
            dotsContainer.addView(dot);
        }
    }

    private void finishGuide() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
        setResult(RESULT_OK);
        finish();
    }

    public static boolean isFirstLaunch(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }
}