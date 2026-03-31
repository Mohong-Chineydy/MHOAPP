package com.mho.web.wv.mohong.utils;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewClientCompat extends WebViewClient {

    private OnPageLoadListener loadListener;

    public interface OnPageLoadListener {
        void onPageStarted(String url);

        void onPageFinished(String url, String title);

        void onReceivedError(String url, int errorCode, String description);
    }

    public WebViewClientCompat(OnPageLoadListener listener) {
        this.loadListener = listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (loadListener != null) {
            loadListener.onPageStarted(url);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (loadListener != null) {
            loadListener.onPageFinished(url, view.getTitle());
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (loadListener != null) {
            loadListener.onReceivedError(failingUrl, errorCode, description);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }
}