package com.mho.web.wv.mohong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mho.web.wv.mohong.manager.PermissionManager;
import com.mho.web.wv.mohong.manager.SettingsManager;
import com.mho.web.wv.mohong.model.AppSettings;
import com.mho.web.wv.mohong.utils.FileManager;

public class SettingsActivity extends AppCompatActivity {

    private SettingsManager settingsManager;
    private FileManager fileManager;
    private AppSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsManager = SettingsManager.getInstance(this);
        fileManager = FileManager.getInstance(this);
        settings = settingsManager.getSettings();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 48, 32, 32);

        // 标题
        TextView title = new TextView(this);
        title.setText("设置");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 32);
        layout.addView(title);

        // 全屏模式开关
        addSwitchRow(layout, "全屏模式", settings.isFullscreen(), checked -> {
            settings.setFullscreen(checked);
            settingsManager.saveSettings();
        });

        // 字体大小调节
        addFontSizeRow(layout);

        // 跟随系统深色模式
        addSwitchRow(layout, "跟随系统深色模式", settings.isFollowSystem(), checked -> {
            settings.setFollowSystem(checked);
            settingsManager.saveSettings();
        });

        // 手动深色模式
        Switch darkModeSwitch = addSwitchRow(layout, "深色模式", settings.isDarkMode(), checked -> {
            settings.setDarkMode(checked);
            settingsManager.saveSettings();
        });
        darkModeSwitch.setEnabled(!settings.isFollowSystem());

        // 主题选择按钮
        Button themeButton = new Button(this);
        themeButton.setText("选择主题颜色");
        themeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ThemePickerActivity.class));
        });
        layout.addView(themeButton);

        // Cookie管理按钮
        Button cookieButton = new Button(this);
        cookieButton.setText("Cookie管理");
        cookieButton.setOnClickListener(v -> {
            startActivity(new Intent(this, CookieManagerActivity.class));
        });
        layout.addView(cookieButton);

        // 权限管理
        Button permissionButton = new Button(this);
        permissionButton.setText("权限管理");
        permissionButton.setOnClickListener(v -> {
            PermissionManager.requestAllPermissions(this);
        });
        layout.addView(permissionButton);

        // 存储路径显示
        TextView pathText = new TextView(this);
        pathText.setText("存储路径: " + FileManager.MHO_ROOT);
        pathText.setTextSize(12);
        pathText.setPadding(0, 32, 0, 0);
        layout.addView(pathText);

        // 版本信息
        TextView versionText = new TextView(this);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            versionText.setText("MHO WEB v" + versionName + " (build " + versionCode + ")");
        } catch (Exception e) {
            versionText.setText("MHO WEB v0.2 Beta");
        }
        versionText.setTextSize(12);
        versionText.setPadding(0, 16, 0, 0);
        layout.addView(versionText);

        // 开发者信息
        TextView developerText = new TextView(this);
        developerText.setText("开发者: Mohong");
        developerText.setTextSize(12);
        layout.addView(developerText);

        setContentView(layout);
    }

    private Switch addSwitchRow(LinearLayout parent, String label, boolean checked,
            CompoundButton.OnCheckedChangeListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 16, 0, 16);

        TextView textView = new TextView(this);
        textView.setText(label);
        textView.setTextSize(16);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textView.setLayoutParams(textParams);

        Switch switchView = new Switch(this);
        switchView.setChecked(checked);
        switchView.setOnCheckedChangeListener(listener);

        row.addView(textView);
        row.addView(switchView);
        parent.addView(row);

        return switchView;
    }

    private void addFontSizeRow(LinearLayout parent) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, 16, 0, 16);

        TextView label = new TextView(this);
        label.setText("字体大小: " + settings.getFontSizePercent() + "%");
        label.setTextSize(14);
        row.addView(label);

        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(150);
        seekBar.setProgress(settings.getFontSizePercent() - 50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int percent = progress + 50;
                label.setText("字体大小: " + percent + "%");
                if (fromUser) {
                    settingsManager.setFontSizePercent(percent);
                    Toast.makeText(SettingsActivity.this, "重启应用生效", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        row.addView(seekBar);

        parent.addView(row);
    }
}