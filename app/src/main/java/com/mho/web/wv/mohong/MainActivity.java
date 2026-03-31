package com.mho.web.wv.mohong;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.mho.web.wv.mohong.adapter.WebViewAdapter;
import com.mho.web.wv.mohong.manager.PermissionManager;
import com.mho.web.wv.mohong.manager.SettingsManager;
import com.mho.web.wv.mohong.manager.TabManager;
import com.mho.web.wv.mohong.model.AppSettings;
import com.mho.web.wv.mohong.model.TabInfo;
import com.mho.web.wv.mohong.ui.FullscreenMenuDialog;
import com.mho.web.wv.mohong.ui.TabBottomSheet;
import com.mho.web.wv.mohong.utils.FileManager;
import com.mho.web.wv.mohong.utils.GestureHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private ViewPager2 viewPager;
    private WebViewAdapter adapter;
    private TabManager tabManager;
    private SettingsManager settingsManager;
    private FileManager fileManager;
    private GestureHelper gestureHelper;
    private FullscreenMenuDialog menuDialog;
    
    private int currentPosition = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化管理器
        fileManager = FileManager.getInstance(this);
        tabManager = TabManager.getInstance(this);
        settingsManager = SettingsManager.getInstance(this);
        
        // 应用设置
        applySettings();
        
        // 创建布局
        FrameLayout layout = new FrameLayout(this);
        
        // 创建ViewPager
        viewPager = new ViewPager2(this);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        
        // 获取标签页列表
        List<TabInfo> tabs = new java.util.ArrayList<>();
        for (int i = 0; i < tabManager.getTabCount(); i++) {
            TabInfo tab = tabManager.getTab(i);
            if (tab != null) {
                tabs.add(tab);
            }
        }
        
        // 创建适配器
        adapter = new WebViewAdapter(tabs, tabManager, viewPager);
        adapter.setOnPageTitleListener((position, title) -> {
            TabInfo tab = tabManager.getTab(position);
            if (tab != null) {
                tab.setTitle(title);
            }
        });
        adapter.setOnPageProgressListener((position, progress) -> {
            // 可在此显示进度
        });
        
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
            }
        });
        
        layout.addView(viewPager);
        setContentView(layout);
        
        // 初始化手势
        setupGestures();
        
        // 恢复保存的滚动位置
        restoreScrollPositions();
    }
    
    private void applySettings() {
        AppSettings settings = settingsManager.getSettings();
        if (settings.isFullscreen()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        
        // 应用字体大小到所有WebView
        float fontSizeScale = settingsManager.getFontSizeScale();
        for (int i = 0; i < tabManager.getTabCount(); i++) {
            WebView wv = tabManager.getWebView(i);
            if (wv != null) {
                wv.getSettings().setTextZoom((int)(fontSizeScale * 100));
            }
        }
    }
    
    private void setupGestures() {
        gestureHelper = new GestureHelper(this, new GestureHelper.OnGestureListener() {
            @Override
            public void onSwipeLeft() {
                if (tabManager.canGoForward(currentPosition)) {
                    tabManager.goForward(currentPosition);
                }
            }
            
            @Override
            public void onSwipeRight() {
                if (tabManager.canGoBack(currentPosition)) {
                    tabManager.goBack(currentPosition);
                }
            }
            
            @Override
            public void onTwoFingerSwipeUp() {
                showTabBottomSheet();
            }
            
            @Override
            public void onTwoFingerSwipeDown() {
                showFullscreenMenu();
            }
        });
        
        viewPager.setOnTouchListener((v, event) -> gestureHelper.onTouchEvent(event));
    }
    
    private void showFullscreenMenu() {
        WebView currentWebView = tabManager.getWebView(currentPosition);
        String currentUrl = currentWebView != null ? currentWebView.getUrl() : "";
        
        menuDialog = new FullscreenMenuDialog(this, settingsManager, tabManager, currentPosition, currentUrl);
        menuDialog.setOnMenuItemClickListener(new FullscreenMenuDialog.OnMenuItemClickListener() {
            @Override
            public void onBack() {
                if (tabManager.canGoBack(currentPosition)) {
                    tabManager.goBack(currentPosition);
                }
            }
            
            @Override
            public void onForward() {
                if (tabManager.canGoForward(currentPosition)) {
                    tabManager.goForward(currentPosition);
                }
            }
            
            @Override
            public void onRefresh() {
                tabManager.reload(currentPosition);
            }
            
            @Override
            public void onHome() {
                String homePath = fileManager.getHomeSvgPath();
                tabManager.loadUrl(currentPosition, homePath);
            }
            
            @Override
            public void onShare() {
                WebView wv = tabManager.getWebView(currentPosition);
                if (wv != null && wv.getUrl() != null) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, wv.getUrl());
                    startActivity(Intent.createChooser(shareIntent, "分享链接"));
                }
            }
            
            @Override
            public void onSettings() {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
            
            @Override
            public void onCookieManager() {
                startActivity(new Intent(MainActivity.this, CookieManagerActivity.class));
            }
            
            @Override
            public void onThemePicker() {
                startActivity(new Intent(MainActivity.this, ThemePickerActivity.class));
            }
            
            @Override
            public void onFullscreenToggle() {
                boolean isFullscreen = settingsManager.isFullscreen();
                settingsManager.setFullscreen(!isFullscreen);
                applySettings();
            }
            
            @Override
            public void onFontSizeChanged(int percent) {
                settingsManager.setFontSizePercent(percent);
                float scale = settingsManager.getFontSizeScale();
                for (int i = 0; i < tabManager.getTabCount(); i++) {
                    WebView wv = tabManager.getWebView(i);
                    if (wv != null) {
                        wv.getSettings().setTextZoom((int)(scale * 100));
                    }
                }
            }
            
            @Override
            public void onGoToUrl(String url) {
                if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("file://")) {
                    url = "https://" + url;
                }
                tabManager.loadUrl(currentPosition, url);
            }
            
            @Override
            public void onOpenTabManager() {
                showTabBottomSheet();
            }
        });
        menuDialog.show();
    }
    
    private void showTabBottomSheet() {
        TabBottomSheet bottomSheet = new TabBottomSheet(this, tabManager, currentPosition);
        bottomSheet.setOnTabSheetListener(new TabBottomSheet.OnTabSheetListener() {
            @Override
            public void onTabSelected(int position) {
                currentPosition = position;
                viewPager.setCurrentItem(position, false);
                
                WebView wv = tabManager.getWebView(position);
                if (wv != null && wv.getUrl() != null && wv.getUrl().isEmpty()) {
                    // 恢复滚动位置
                    TabInfo tab = tabManager.getTab(position);
                    if (tab != null && (tab.getScrollX() != 0 || tab.getScrollY() != 0)) {
                        wv.scrollTo(tab.getScrollX(), tab.getScrollY());
                    }
                }
            }
            
            @Override
            public void onNewTab() {
                int newIndex = tabManager.createNewTab(fileManager.getHomeSvgPath());
                if (newIndex != -1) {
                    adapter.updateTabs(getCurrentTabs());
                    viewPager.setAdapter(adapter);
                    currentPosition = newIndex;
                    viewPager.setCurrentItem(currentPosition, false);
                } else {
                    Toast.makeText(MainActivity.this, "最多支持50个标签页", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onUrlSubmit(String url) {
                if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("file://")) {
                    url = "https://" + url;
                }
                tabManager.loadUrl(currentPosition, url);
            }
        });
        bottomSheet.show();
    }
    
    private List<TabInfo> getCurrentTabs() {
        List<TabInfo> tabs = new java.util.ArrayList<>();
        for (int i = 0; i < tabManager.getTabCount(); i++) {
            TabInfo tab = tabManager.getTab(i);
            if (tab != null) {
                tabs.add(tab);
            }
        }
        return tabs;
    }
    
    private void restoreScrollPositions() {
        for (int i = 0; i < tabManager.getTabCount(); i++) {
            TabInfo tab = tabManager.getTab(i);
            WebView wv = tabManager.getWebView(i);
            if (tab != null && wv != null && (tab.getScrollX() != 0 || tab.getScrollY() != 0)) {
                wv.post(() -> wv.scrollTo(tab.getScrollX(), tab.getScrollY()));
            }
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // 保存所有标签页状态
        for (int i = 0; i < tabManager.getTabCount(); i++) {
            WebView wv = tabManager.getWebView(i);
            TabInfo tab = tabManager.getTab(i);
            if (wv != null && tab != null) {
                tab.setScrollX(wv.getScrollX());
                tab.setScrollY(wv.getScrollY());
                tab.setScale(wv.getScale());
            }
        }
        tabManager.saveTabs();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        applySettings();
    }
    
    @Override
    public void onBackPressed() {
        if (tabManager.canGoBack(currentPosition)) {
            tabManager.goBack(currentPosition);
        } else if (tabManager.getTabCount() > 1) {
            tabManager.closeTab(currentPosition);
            if (currentPosition >= tabManager.getTabCount()) {
                currentPosition = tabManager.getTabCount() - 1;
            }
            adapter.updateTabs(getCurrentTabs());
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(currentPosition, false);
        } else {
            finish();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabManager.saveTabs();
    }
}