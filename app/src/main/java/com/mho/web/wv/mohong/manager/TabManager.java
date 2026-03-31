package com.mho.web.wv.mohong.manager;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.mho.web.wv.mohong.model.TabInfo;
import com.mho.web.wv.mohong.utils.FileManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TabManager {
    
    private static final String TAG = "TabManager";
    private static final int MAX_TABS = 50;
    
    private static TabManager instance;
    private Context context;
    private FileManager fileManager;
    private List<TabInfo> tabs;
    private List<WebView> webViews;
    private int nextId;
    
    private TabManager(Context context) {
        this.context = context.getApplicationContext();
        this.fileManager = FileManager.getInstance(context);
        this.tabs = new ArrayList<>();
        this.webViews = new ArrayList<>();
        this.nextId = 0;
        loadTabs();
    }
    
    public static synchronized TabManager getInstance(Context context) {
        if (instance == null) {
            instance = new TabManager(context);
        }
        return instance;
    }
    
    public void loadTabs() {
        try {
            JSONArray tabsArray = fileManager.loadTabsList();
            tabs.clear();
            webViews.clear();
            
            for (int i = 0; i < tabsArray.length(); i++) {
                JSONObject tabJson = tabsArray.getJSONObject(i);
                TabInfo tab = TabInfo.fromJson(tabJson);
                tabs.add(tab);
                webViews.add(null);
                if (tab.getId() >= nextId) {
                    nextId = tab.getId() + 1;
                }
            }
            
            Log.d(TAG, "加载标签页成功，数量: " + tabs.size());
        } catch (Exception e) {
            Log.e(TAG, "加载标签页失败", e);
            if (tabs.isEmpty()) {
                createNewTab("about:blank");
            }
        }
    }
    
    public void saveTabs() {
        try {
            JSONArray tabsArray = new JSONArray();
            for (int i = 0; i < tabs.size(); i++) {
                tabsArray.put(tabs.get(i).toJson());
                if (webViews.get(i) != null) {
                    WebView wv = webViews.get(i);
                    tabs.get(i).setUrl(wv.getUrl());
                    tabs.get(i).setTitle(wv.getTitle());
                    tabs.get(i).setScrollX(wv.getScrollX());
                    tabs.get(i).setScrollY(wv.getScrollY());
                    tabs.get(i).setScale(wv.getScale());
                    fileManager.saveTabState(i, tabs.get(i).toJson());
                }
            }
            fileManager.saveTabsList(tabsArray);
            Log.d(TAG, "保存标签页成功，数量: " + tabs.size());
        } catch (Exception e) {
            Log.e(TAG, "保存标签页失败", e);
        }
    }
    
    public int createNewTab(String url) {
        if (tabs.size() >= MAX_TABS) {
            Log.w(TAG, "已达到最大标签页数量: " + MAX_TABS);
            return -1;
        }
        
        TabInfo newTab = new TabInfo(nextId++, url);
        tabs.add(newTab);
        webViews.add(null);
        saveTabs();
        Log.d(TAG, "创建新标签页: id=" + newTab.getId() + ", url=" + url);
        return tabs.size() - 1;
    }
    
    public void closeTab(int index) {
        if (index < 0 || index >= tabs.size()) {
            return;
        }
        
        TabInfo tab = tabs.get(index);
        WebView wv = webViews.get(index);
        
        if (wv != null) {
            wv.stopLoading();
            wv.destroy();
        }
        
        tabs.remove(index);
        webViews.remove(index);
        fileManager.deleteTabState(index);
        
        for (int i = index; i < tabs.size(); i++) {
            try {
                fileManager.saveTabState(i, tabs.get(i).toJson());
            } catch (Exception e) {
                Log.e(TAG, "保存标签页状态失败", e);
            }
        }
        
        saveTabs();
        Log.d(TAG, "关闭标签页: id=" + tab.getId());
    }
    
    public TabInfo getTab(int index) {
        if (index >= 0 && index < tabs.size()) {
            return tabs.get(index);
        }
        return null;
    }
    
    public WebView getWebView(int index) {
        if (index >= 0 && index < webViews.size()) {
            return webViews.get(index);
        }
        return null;
    }
    
    public void setWebView(int index, WebView webView) {
        if (index >= 0 && index < webViews.size()) {
            webViews.set(index, webView);
        }
    }
    
    public int getTabCount() {
        return tabs.size();
    }
    
    public void updateTabInfo(int index, String url, String title) {
        if (index >= 0 && index < tabs.size()) {
            tabs.get(index).setUrl(url);
            tabs.get(index).setTitle(title);
            saveTabs();
        }
    }
    
    public void updateTabScroll(int index, int scrollX, int scrollY, float scale) {
        if (index >= 0 && index < tabs.size()) {
            tabs.get(index).setScrollX(scrollX);
            tabs.get(index).setScrollY(scrollY);
            tabs.get(index).setScale(scale);
        }
    }
    
    public void clearAllTabs() {
        for (WebView wv : webViews) {
            if (wv != null) {
                wv.destroy();
            }
        }
        tabs.clear();
        webViews.clear();
        fileManager.clearAllTabsState();
        createNewTab("about:blank");
        saveTabs();
    }
    
    public boolean canGoBack(int index) {
        WebView wv = getWebView(index);
        return wv != null && wv.canGoBack();
    }
    
    public void goBack(int index) {
        WebView wv = getWebView(index);
        if (wv != null && wv.canGoBack()) {
            wv.goBack();
        }
    }
    
    public boolean canGoForward(int index) {
        WebView wv = getWebView(index);
        return wv != null && wv.canGoForward();
    }
    
    public void goForward(int index) {
        WebView wv = getWebView(index);
        if (wv != null && wv.canGoForward()) {
            wv.goForward();
        }
    }
    
    public void reload(int index) {
        WebView wv = getWebView(index);
        if (wv != null) {
            wv.reload();
        }
    }
    
    public void loadUrl(int index, String url) {
        WebView wv = getWebView(index);
        if (wv != null) {
            wv.loadUrl(url);
            tabs.get(index).setUrl(url);
        }
    }
}