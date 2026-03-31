package com.mho.web.wv.mohong.model;

import org.json.JSONException;
import org.json.JSONObject;

public class TabInfo {
    private int id;
    private String url;
    private String title;
    private int scrollX;
    private int scrollY;
    private float scale;
    private long createdAt;
    
    private static final String JSON_ID = "id";
    private static final String JSON_URL = "url";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SCROLL_X = "scrollX";
    private static final String JSON_SCROLL_Y = "scrollY";
    private static final String JSON_SCALE = "scale";
    private static final String JSON_CREATED_AT = "createdAt";
    
    public TabInfo() {
        this.id = -1;
        this.url = "about:blank";
        this.title = "新标签页";
        this.scrollX = 0;
        this.scrollY = 0;
        this.scale = 1.0f;
        this.createdAt = System.currentTimeMillis();
    }
    
    public TabInfo(int id, String url) {
        this();
        this.id = id;
        this.url = url;
    }
    
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, id);
        json.put(JSON_URL, url);
        json.put(JSON_TITLE, title);
        json.put(JSON_SCROLL_X, scrollX);
        json.put(JSON_SCROLL_Y, scrollY);
        json.put(JSON_SCALE, scale);
        json.put(JSON_CREATED_AT, createdAt);
        return json;
    }
    
    public static TabInfo fromJson(JSONObject json) throws JSONException {
        TabInfo tab = new TabInfo();
        tab.id = json.getInt(JSON_ID);
        tab.url = json.getString(JSON_URL);
        tab.title = json.optString(JSON_TITLE, "新标签页");
        tab.scrollX = json.optInt(JSON_SCROLL_X, 0);
        tab.scrollY = json.optInt(JSON_SCROLL_Y, 0);
        tab.scale = (float) json.optDouble(JSON_SCALE, 1.0);
        tab.createdAt = json.optLong(JSON_CREATED_AT, System.currentTimeMillis());
        return tab;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getScrollX() { return scrollX; }
    public void setScrollX(int scrollX) { this.scrollX = scrollX; }
    public int getScrollY() { return scrollY; }
    public void setScrollY(int scrollY) { this.scrollY = scrollY; }
    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = scale; }
    public long getCreatedAt() { return createdAt; }
}
