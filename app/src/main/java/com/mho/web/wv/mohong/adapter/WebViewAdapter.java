package com.mho.web.wv.mohong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.mho.web.wv.mohong.manager.TabManager;
import com.mho.web.wv.mohong.model.TabInfo;

import java.util.List;

public class WebViewAdapter extends RecyclerView.Adapter<WebViewAdapter.WebViewHolder> {

    private List<TabInfo> tabs;
    private TabManager tabManager;
    private ViewPager2 viewPager;
    private OnPageTitleListener titleListener;
    private OnPageProgressListener progressListener;

    public interface OnPageTitleListener {
        void onTitleChanged(int position, String title);
    }

    public interface OnPageProgressListener {
        void onProgressChanged(int position, int progress);
    }

    public WebViewAdapter(List<TabInfo> tabs, TabManager tabManager, ViewPager2 viewPager) {
        this.tabs = tabs;
        this.tabManager = tabManager;
        this.viewPager = viewPager;
    }

    public void setOnPageTitleListener(OnPageTitleListener listener) {
        this.titleListener = listener;
    }

    public void setOnPageProgressListener(OnPageProgressListener listener) {
        this.progressListener = listener;
    }

    @NonNull
    @Override
    public WebViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout container = new FrameLayout(parent.getContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
        return new WebViewHolder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull WebViewHolder holder, int position) {
        WebView webView = tabManager.getWebView(position);

        if (webView == null) {
            webView = new WebView(holder.itemView.getContext());
            setupWebView(webView, position);
            tabManager.setWebView(position, webView);

            TabInfo tab = tabs.get(position);
            webView.loadUrl(tab.getUrl());

            if (tab.getScrollX() != 0 || tab.getScrollY() != 0) {
                webView.scrollTo(tab.getScrollX(), tab.getScrollY());
            }
            if (tab.getScale() != 1.0f) {
                webView.setInitialScale((int) (tab.getScale() * 100));
            }
        }

        if (webView.getParent() != null) {
            ((ViewGroup) webView.getParent()).removeView(webView);
        }
        holder.container.addView(webView);
    }

    private void setupWebView(WebView webView, int position) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDefaultTextEncodingName("utf-8");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = view.getTitle();
                if (title != null && titleListener != null) {
                    titleListener.onTitleChanged(position, title);
                }
                tabManager.updateTabInfo(position, url, title);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (progressListener != null) {
                    progressListener.onProgressChanged(position, newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (titleListener != null) {
                    titleListener.onTitleChanged(position, title);
                }
                tabManager.updateTabInfo(position, view.getUrl(), title);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tabs.size();
    }

    public static class WebViewHolder extends RecyclerView.ViewHolder {
        FrameLayout container;

        public WebViewHolder(@NonNull FrameLayout itemView) {
            super(itemView);
            this.container = itemView;
        }
    }

    public void updateTabs(List<TabInfo> newTabs) {
        this.tabs = newTabs;
        notifyDataSetChanged();
    }
}