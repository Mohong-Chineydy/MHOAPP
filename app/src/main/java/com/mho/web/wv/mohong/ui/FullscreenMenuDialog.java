package com.mho.web.wv.mohong.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mho.web.wv.mohong.manager.SettingsManager;
import com.mho.web.wv.mohong.manager.TabManager;

public class FullscreenMenuDialog extends Dialog {

    private Context context;
    private OnMenuItemClickListener listener;
    private SettingsManager settingsManager;
    private TabManager tabManager;
    private int currentTabIndex;
    private String currentUrl;

    public interface OnMenuItemClickListener {
        void onBack();

        void onForward();

        void onRefresh();

        void onHome();

        void onShare();

        void onSettings();

        void onCookieManager();

        void onThemePicker();

        void onFullscreenToggle();

        void onFontSizeChanged(int percent);

        void onGoToUrl(String url);

        void onOpenTabManager();
    }

    public FullscreenMenuDialog(Context context, SettingsManager settingsManager,
            TabManager tabManager, int currentTabIndex, String currentUrl) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;
        this.settingsManager = settingsManager;
        this.tabManager = tabManager;
        this.currentTabIndex = currentTabIndex;
        this.currentUrl = currentUrl;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(createContentView());

        findViewById(android.R.id.content).setOnClickListener(v -> dismiss());
    }

    private View createContentView() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xE6000000);
        layout.setPadding(32, 64, 32, 64);

        TextView urlText = new TextView(context);
        urlText.setText(currentUrl);
        urlText.setTextColor(0xFFFFFFFF);
        urlText.setTextSize(14);
        urlText.setPadding(0, 0, 0, 32);
        layout.addView(urlText);

        LinearLayout urlLayout = new LinearLayout(context);
        urlLayout.setOrientation(LinearLayout.HORIZONTAL);
        urlLayout.setPadding(0, 0, 0, 24);

        EditText urlInput = new EditText(context);
        urlInput.setHint("输入网址");
        urlInput.setText(currentUrl);
        urlInput.setTextColor(0xFFFFFFFF);
        urlInput.setHintTextColor(0x88FFFFFF);
        urlInput.setBackgroundColor(0x33FFFFFF);
        urlInput.setPadding(16, 12, 16, 12);
        LinearLayout.LayoutParams urlParams = new LinearLayout.LayoutParams(0,
        LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        urlParams.setMargins(0, 0, 16, 0);
        urlInput.setLayoutParams(urlParams);

        Button goButton = new Button(context);
        goButton.setText("前往");
        goButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGoToUrl(urlInput.getText().toString());
                dismiss();
            }
        });

        urlLayout.addView(urlInput);
        urlLayout.addView(goButton);
        layout.addView(urlLayout);

        LinearLayout row1 = createButtonRow();
        addButton(row1, "后退", v -> {
            if (listener != null) listener.onBack();
            dismiss();
        });
        addButton(row1, "前进", v -> {
            if (listener != null) listener.onForward();
            dismiss();
        });
        addButton(row1, "刷新", v -> {
            if (listener != null) listener.onRefresh();
            dismiss();
        });
        addButton(row1, "主页", v -> {
            if (listener != null) listener.onHome();
            dismiss();
        });
        layout.addView(row1);

        LinearLayout row2 = createButtonRow();
        addButton(row2, "分享", v -> {
            if (listener != null) listener.onShare();
            dismiss();
        });
        addButton(row2, "标签管理", v -> {
            if (listener != null) listener.onOpenTabManager();
            dismiss();
        });
        addButton(row2, "全屏", v -> {
            if (listener != null) listener.onFullscreenToggle();
            dismiss();
        });
        layout.addView(row2);

        LinearLayout row3 = createButtonRow();
        addButton(row3, "设置", v -> {
            if (listener != null) listener.onSettings();
            dismiss();
        });
        addButton(row3, "主题", v -> {
            if (listener != null) listener.onThemePicker();
            dismiss();
        });
        addButton(row3, "Cookie", v -> {
            if (listener != null) listener.onCookieManager();
            dismiss();
        });
        layout.addView(row3);

        TextView fontSizeLabel = new TextView(context);
        fontSizeLabel.setText("字体大小: " + settingsManager.getFontSizePercent() + "%");
        fontSizeLabel.setTextColor(0xFFFFFFFF);
        fontSizeLabel.setPadding(0, 32, 0, 16);
        layout.addView(fontSizeLabel);

        SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(150);
        seekBar.setProgress(settingsManager.getFontSizePercent() - 50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int percent = progress + 50;
                fontSizeLabel.setText("字体大小: " + percent + "%");
                if (fromUser && listener != null) {
                    listener.onFontSizeChanged(percent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        layout.addView(seekBar);

        Button closeButton = new Button(context);
        closeButton.setText("关闭菜单");
        closeButton.setOnClickListener(v -> dismiss());
        layout.addView(closeButton);

        return layout;
    }

    private LinearLayout createButtonRow() {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 0, 0, 16);
        return row;
    }

    private void addButton(LinearLayout row, String text, View.OnClickListener listener) {
        Button button = new Button(context);
        button.setText(text);
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
        LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(8, 0, 8, 0);
        button.setLayoutParams(params);
        row.addView(button);
    }
}