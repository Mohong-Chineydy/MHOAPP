package com.mho.web.wv.mohong.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileManager {
    
    private static final String TAG = "FileManager";
    
    public static final String MHO_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MHO/WEB";
    
    private static final String DIR_SETTINGS = "settings";
    private static final String DIR_TABS = "tabs";
    private static final String DIR_COOKIES = "cookies";
    private static final String DIR_DOWNLOADS = "downloads";
    private static final String DIR_OPSVG = "opSVG";
    private static final String DIR_LOGS = "logs";
    
    private static final String FILE_CONFIG = "config.json";
    private static final String FILE_FONT_SIZE = "font_size.txt";
    private static final String FILE_TABS_LIST = "tabs_list.json";
    private static final String FILE_COOKIES_BACKUP = "cookies_backup.json";
    private static final String FILE_TAB_PREFIX = "tab_";
    private static final String FILE_LOG_PREFIX = "app_";
    
    private Context context;
    private static FileManager instance;
    
    private FileManager(Context context) {
        this.context = context.getApplicationContext();
        initDirectories();
    }
    
    public static synchronized FileManager getInstance(Context context) {
        if (instance == null) {
            instance = new FileManager(context);
        }
        return instance;
    }
    
    private void initDirectories() {
        createDir(MHO_ROOT);
        createDir(MHO_ROOT + "/" + DIR_SETTINGS);
        createDir(MHO_ROOT + "/" + DIR_TABS);
        createDir(MHO_ROOT + "/" + DIR_COOKIES);
        createDir(MHO_ROOT + "/" + DIR_DOWNLOADS);
        createDir(MHO_ROOT + "/" + DIR_OPSVG);
        createDir(MHO_ROOT + "/" + DIR_LOGS);
        copyDefaultAssets();
        Log.d(TAG, "初始化目录: " + MHO_ROOT);
    }
    
    private void createDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    private void copyDefaultAssets() {
        File splashFile = new File(getOpSvgDir(), "splash.svg");
        File homeFile = new File(getOpSvgDir(), "home.svg");
        if (!splashFile.exists()) {
            copyAssetToPublic("splash.svg", DIR_OPSVG + "/splash.svg");
        }
        if (!homeFile.exists()) {
            copyAssetToPublic("home.svg", DIR_OPSVG + "/home.svg");
        }
    }
    
    public void saveSettings(JSONObject settings) throws IOException {
        writeFile(DIR_SETTINGS + "/" + FILE_CONFIG, settings.toString());
    }
    
    public JSONObject loadSettings() {
        try {
            String content = readFile(DIR_SETTINGS + "/" + FILE_CONFIG);
            if (content != null && !content.isEmpty()) {
                return new JSONObject(content);
            }
        } catch (Exception e) {
            Log.e(TAG, "加载设置失败", e);
        }
        return null;
    }
    
    public void saveFontSize(int fontSizePercent) throws IOException {
        writeFile(DIR_SETTINGS + "/" + FILE_FONT_SIZE, String.valueOf(fontSizePercent));
    }
    
    public int loadFontSize() {
        try {
            String content = readFile(DIR_SETTINGS + "/" + FILE_FONT_SIZE);
            if (content != null) {
                return Integer.parseInt(content.trim());
            }
        } catch (Exception e) {
            Log.e(TAG, "读取字体大小失败", e);
        }
        return 100;
    }
    
    public void saveTabsList(JSONArray tabsArray) throws IOException {
        writeFile(DIR_TABS + "/" + FILE_TABS_LIST, tabsArray.toString());
    }
    
    public JSONArray loadTabsList() {
        try {
            String content = readFile(DIR_TABS + "/" + FILE_TABS_LIST);
            if (content != null) {
                return new JSONArray(content);
            }
        } catch (Exception e) {
            Log.e(TAG, "读取标签页列表失败", e);
        }
        return new JSONArray();
    }
    
    public void saveTabState(int index, JSONObject tabState) throws IOException {
        writeFile(DIR_TABS + "/" + FILE_TAB_PREFIX + index + ".json", tabState.toString());
    }
    
    public JSONObject loadTabState(int index) {
        try {
            String content = readFile(DIR_TABS + "/" + FILE_TAB_PREFIX + index + ".json");
            if (content != null) {
                return new JSONObject(content);
            }
        } catch (Exception e) {
            Log.e(TAG, "读取标签页状态失败: index=" + index, e);
        }
        return null;
    }
    
    public void deleteTabState(int index) {
        deleteFile(DIR_TABS + "/" + FILE_TAB_PREFIX + index + ".json");
    }
    
    public void clearAllTabsState() {
        deleteDir(DIR_TABS);
        createDir(MHO_ROOT + "/" + DIR_TABS);
    }
    
    public void saveCookiesBackup(String cookiesJson) throws IOException {
        writeFile(DIR_COOKIES + "/" + FILE_COOKIES_BACKUP, cookiesJson);
    }
    
    public String loadCookiesBackup() {
        try {
            return readFile(DIR_COOKIES + "/" + FILE_COOKIES_BACKUP);
        } catch (Exception e) {
            Log.e(TAG, "读取Cookie备份失败", e);
        }
        return null;
    }
    
    public void writeLog(String message) {
        try {
            String dateStr = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
            String timeStr = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            String logLine = timeStr + " - " + message + "\n";
            File logFile = new File(MHO_ROOT + "/" + DIR_LOGS, FILE_LOG_PREFIX + dateStr + ".log");
            FileWriter writer = new FileWriter(logFile, true);
            writer.append(logLine);
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, "写入日志失败", e);
        }
    }
    
    public File getDownloadDir() {
        File dir = new File(MHO_ROOT + "/" + DIR_DOWNLOADS);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    
    public File getOpSvgDir() {
        File dir = new File(MHO_ROOT + "/" + DIR_OPSVG);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    
    public String getSplashSvgPath() {
        File userSvg = new File(getOpSvgDir(), "splash.svg");
        if (userSvg.exists()) {
            return "file://" + userSvg.getAbsolutePath();
        }
        return "file:///android_asset/splash.svg";
    }
    
    public String getHomeSvgPath() {
        File userSvg = new File(getOpSvgDir(), "home.svg");
        if (userSvg.exists()) {
            return "file://" + userSvg.getAbsolutePath();
        }
        return "file:///android_asset/home.svg";
    }
    
    private void writeFile(String relativePath, String content) throws IOException {
        String fullPath = MHO_ROOT + "/" + relativePath;
        File file = new File(fullPath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    private String readFile(String relativePath) throws IOException {
        String fullPath = MHO_ROOT + "/" + relativePath;
        File file = new File(fullPath);
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return new String(data, StandardCharsets.UTF_8);
        }
    }
    
    private void deleteFile(String relativePath) {
        File file = new File(MHO_ROOT + "/" + relativePath);
        if (file.exists()) {
            file.delete();
        }
    }
    
    private void deleteDir(String relativePath) {
        File dir = new File(MHO_ROOT + "/" + relativePath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            dir.delete();
        }
    }
    
    private void copyAssetToPublic(String assetPath, String destRelativePath) {
        try {
            InputStream is = context.getAssets().open(assetPath);
            File destFile = new File(MHO_ROOT + "/" + destRelativePath);
            File parent = destFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (OutputStream os = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            }
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "复制assets文件失败: " + assetPath, e);
        }
    }
}