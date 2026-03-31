package com.mho.web.wv.mohong.manager;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.mho.web.wv.mohong.model.AppSettings;
import com.mho.web.wv.mohong.utils.FileManager;

import org.json.JSONObject;

public class SettingsManager {

    private static final String TAG = "SettingsManager";
    private static SettingsManager instance;
    private Context context;
    private AppSettings settings;
    private FileManager fileManager;

    private SettingsManager(Context context) {
        this.context = context.getApplicationContext();
        this.fileManager = FileManager.getInstance(context);
        loadSettings();
    }

    public static synchronized SettingsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SettingsManager(context);
        }
        return instance;
    }

    private void loadSettings() {
        try {
            JSONObject json = fileManager.loadSettings();
            if (json != null) {
                settings = AppSettings.fromJson(json);
                Log.d(TAG, "加载设置成功");
            } else {
                settings = new AppSettings();
                Log.d(TAG, "使用默认设置");
            }
        } catch (Exception e) {
            Log.e(TAG, "加载设置失败", e);
            settings = new AppSettings();
        }
    }

    public void saveSettings() {
        try {
            JSONObject json = settings.toJson();
            fileManager.saveSettings(json);
            fileManager.saveFontSize(settings.getFontSizePercent());
            applyTheme();
            Log.d(TAG, "保存设置成功");
        } catch (Exception e) {
            Log.e(TAG, "保存设置失败", e);
        }
    }

    public void applyTheme() {
        int themeColor = settings.getThemeRgb();
        boolean isDarkMode = settings.isDarkMode();
        boolean followSystem = settings.isFollowSystem();

        if (followSystem) {
            int nightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            isDarkMode = nightMode == Configuration.UI_MODE_NIGHT_YES;
        }
    }

    public AppSettings getSettings() {
        return settings;
    }

    public int getThemeRgb() {
        return settings.getThemeRgb();
    }

    public void setThemeRgb(int rgb) {
        settings.setThemeRgb(rgb);
        saveSettings();
    }

    public boolean isDarkMode() {
        return settings.isDarkMode();
    }

    public void setDarkMode(boolean darkMode) {
        settings.setDarkMode(darkMode);
        saveSettings();
    }

    public boolean isFollowSystem() {
        return settings.isFollowSystem();
    }

    public void setFollowSystem(boolean followSystem) {
        settings.setFollowSystem(followSystem);
        saveSettings();
    }

    public int getFontSizePercent() {
        return settings.getFontSizePercent();
    }

    public void setFontSizePercent(int percent) {
        settings.setFontSizePercent(percent);
        saveSettings();
    }

    public float getFontSizeScale() {
        return settings.getFontSizePercent() / 100.0f;
    }

    public boolean isFullscreen() {
        return settings.isFullscreen();
    }

    public void setFullscreen(boolean fullscreen) {
        settings.setFullscreen(fullscreen);
        saveSettings();
    }
}