package com.mho.web.wv.mohong.model;

import org.json.JSONException;
import org.json.JSONObject;

public class AppSettings {
    
    private int themeRgb;           // 主题色RGB值
    private boolean darkMode;       // 深色模式
    private boolean followSystem;   // 跟随系统深色模式
    private int fontSizePercent;    // 字体大小百分比 50-200
    private boolean fullscreen;     // 全屏模式
    
    private static final String JSON_THEME_RGB = "themeRgb";
    private static final String JSON_DARK_MODE = "darkMode";
    private static final String JSON_FOLLOW_SYSTEM = "followSystem";
    private static final String JSON_FONT_SIZE = "fontSizePercent";
    private static final String JSON_FULLSCREEN = "fullscreen";
    
    public AppSettings() {
        this.themeRgb = 0x2196F3;
        this.darkMode = false;
        this.followSystem = true;
        this.fontSizePercent = 100;
        this.fullscreen = true;
    }
    
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_THEME_RGB, themeRgb);
        json.put(JSON_DARK_MODE, darkMode);
        json.put(JSON_FOLLOW_SYSTEM, followSystem);
        json.put(JSON_FONT_SIZE, fontSizePercent);
        json.put(JSON_FULLSCREEN, fullscreen);
        return json;
    }
    
    public static AppSettings fromJson(JSONObject json) throws JSONException {
        AppSettings settings = new AppSettings();
        settings.themeRgb = json.optInt(JSON_THEME_RGB, 0x2196F3);
        settings.darkMode = json.optBoolean(JSON_DARK_MODE, false);
        settings.followSystem = json.optBoolean(JSON_FOLLOW_SYSTEM, true);
        settings.fontSizePercent = json.optInt(JSON_FONT_SIZE, 100);
        settings.fullscreen = json.optBoolean(JSON_FULLSCREEN, true);
        return settings;
    }
    
    // Getters and Setters
    public int getThemeRgb() { return themeRgb; }
    public void setThemeRgb(int themeRgb) { this.themeRgb = themeRgb; }
    public boolean isDarkMode() { return darkMode; }
    public void setDarkMode(boolean darkMode) { this.darkMode = darkMode; }
    public boolean isFollowSystem() { return followSystem; }
    public void setFollowSystem(boolean followSystem) { this.followSystem = followSystem; }
    public int getFontSizePercent() { return fontSizePercent; }
    public void setFontSizePercent(int fontSizePercent) { this.fontSizePercent = fontSizePercent; }
    public boolean isFullscreen() { return fullscreen; }
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }
}
