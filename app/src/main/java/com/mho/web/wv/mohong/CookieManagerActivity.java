package com.mho.web.wv.mohong;

import android.app.AlertDialog;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mho.web.wv.mohong.utils.FileManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CookieManagerActivity extends AppCompatActivity {

    private FileManager fileManager;
    private LinearLayout cookiesListLayout;
    private List<CookieInfo> cookiesList;
    private EditText searchInput;

    private static class CookieInfo {
        String domain;
        String name;
        String value;
        String path;

        CookieInfo(String domain, String name, String value, String path) {
            this.domain = domain;
            this.name = name;
            this.value = value;
            this.path = path;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileManager = FileManager.getInstance(this);
        cookiesList = new ArrayList<>();

        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 48, 32, 32);

        TextView title = new TextView(this);
        title.setText("Cookie管理");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 32);
        layout.addView(title);

        // 搜索框
        LinearLayout searchLayout = new LinearLayout(this);
        searchLayout.setOrientation(LinearLayout.HORIZONTAL);
        searchLayout.setPadding(0, 0, 0, 16);

        searchInput = new EditText(this);
        searchInput.setHint("搜索域名");
        searchInput.setSingleLine(true);
        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(
        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        searchParams.setMargins(0, 0, 16, 0);
        searchInput.setLayoutParams(searchParams);

        Button searchButton = new Button(this);
        searchButton.setText("搜索");
        searchButton.setOnClickListener(v -> refreshCookieList(searchInput.getText().toString()));

        searchLayout.addView(searchInput);
        searchLayout.addView(searchButton);
        layout.addView(searchLayout);

        // 按钮行
        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setPadding(0, 0, 0, 24);

        Button refreshButton = new Button(this);
        refreshButton.setText("刷新");
        refreshButton.setOnClickListener(v -> refreshCookieList(searchInput.getText().toString()));

        Button clearAllButton = new Button(this);
        clearAllButton.setText("清除全部");
        clearAllButton.setOnClickListener(v -> clearAllCookies());

        Button exportButton = new Button(this);
        exportButton.setText("导出");
        exportButton.setOnClickListener(v -> exportCookies());

        Button importButton = new Button(this);
        importButton.setText("导入");
        importButton.setOnClickListener(v -> importCookies());

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        btnParams.setMargins(8, 0, 8, 0);

        refreshButton.setLayoutParams(btnParams);
        clearAllButton.setLayoutParams(btnParams);
        exportButton.setLayoutParams(btnParams);
        importButton.setLayoutParams(btnParams);

        buttonRow.addView(refreshButton);
        buttonRow.addView(clearAllButton);
        buttonRow.addView(exportButton);
        buttonRow.addView(importButton);
        layout.addView(buttonRow);

        // Cookie列表区域
        TextView listTitle = new TextView(this);
        listTitle.setText("Cookie列表");
        listTitle.setTextSize(18);
        listTitle.setPadding(0, 16, 0, 16);
        layout.addView(listTitle);

        cookiesListLayout = new LinearLayout(this);
        cookiesListLayout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(cookiesListLayout);

        scrollView.addView(layout);
        setContentView(scrollView);

        // 加载Cookie列表
        refreshCookieList("");
    }

    private void refreshCookieList(String filter) {
        cookiesListLayout.removeAllViews();
        cookiesList.clear();

        CookieManager cookieManager = CookieManager.getInstance();
        String cookieString = cookieManager.getCookie("https://");

        if (cookieString == null || cookieString.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("暂无Cookie");
            emptyText.setPadding(0, 32, 0, 32);
            emptyText.setGravity(android.view.Gravity.CENTER);
            cookiesListLayout.addView(emptyText);
            return;
        }

        String[] cookies = cookieString.split(";");
        for (String cookie : cookies) {
            String[] parts = cookie.trim().split("=", 2);
            if (parts.length == 2) {
                String name = parts[0];
                String value = parts[1];
                CookieInfo info = new CookieInfo("当前域名", name, value, "/");

                if (filter.isEmpty() || info.domain.contains(filter) || info.name.contains(filter)) {
                    cookiesList.add(info);
                    addCookieItem(info);
                }
            }
        }
    }

    private void addCookieItem(CookieInfo info) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(16, 16, 16, 16);
        itemLayout.setBackgroundColor(0x1F000000);
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT);
        itemParams.setMargins(0, 0, 0, 8);
        itemLayout.setLayoutParams(itemParams);

        TextView domainText = new TextView(this);
        domainText.setText("域名: " + info.domain);
        domainText.setTextSize(12);
        domainText.setTextColor(0xFF888888);

        TextView nameText = new TextView(this);
        nameText.setText("名称: " + info.name);
        nameText.setTextSize(14);

        TextView valueText = new TextView(this);
        valueText.setText("值: " + (info.value.length() > 50 ? info.value.substring(0, 50) + "..." : info.value));
        valueText.setTextSize(12);
        valueText.setTextColor(0xFF666666);

        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setPadding(0, 8, 0, 0);

        Button deleteButton = new Button(this);
        deleteButton.setText("删除");
        deleteButton.setTextSize(12);
        deleteButton.setOnClickListener(v -> {
            CookieManager cookieManager = CookieManager.getInstance();
            String cookieUrl = "https://" + info.domain;
            cookieManager.setCookie(cookieUrl, info.name + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT");
            cookieManager.flush();
            refreshCookieList(searchInput.getText().toString());
            Toast.makeText(this, "已删除: " + info.name, Toast.LENGTH_SHORT).show();
        });

        buttonRow.addView(deleteButton);

        itemLayout.addView(domainText);
        itemLayout.addView(nameText);
        itemLayout.addView(valueText);
        itemLayout.addView(buttonRow);

        cookiesListLayout.addView(itemLayout);
    }

    private void clearAllCookies() {
        new AlertDialog.Builder(this)
                .setTitle("清除所有Cookie")
                .setMessage("确定要清除所有Cookie吗？此操作不可撤销。")
                .setPositiveButton("确定", (dialog, which) -> {
                    CookieManager cookieManager = CookieManager.getInstance();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        cookieManager.removeAllCookies(null);
                    } else {
                        cookieManager.removeAllCookie();
                    }
                    cookieManager.flush();
                    refreshCookieList(searchInput.getText().toString());
                    Toast.makeText(this, "已清除所有Cookie", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void exportCookies() {
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            String cookieString = cookieManager.getCookie("https://");

            JSONArray jsonArray = new JSONArray();
            if (cookieString != null && !cookieString.isEmpty()) {
                String[] cookies = cookieString.split(";");
                for (String cookie : cookies) {
                    String[] parts = cookie.trim().split("=", 2);
                    if (parts.length == 2) {
                        JSONObject obj = new JSONObject();
                        obj.put("name", parts[0]);
                        obj.put("value", parts[1]);
                        obj.put("domain", "current");
                        jsonArray.put(obj);
                    }
                }
            }

            fileManager.saveCookiesBackup(jsonArray.toString());
            Toast.makeText(this, "Cookie已导出到: " + FileManager.MHO_ROOT + "/cookies/", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void importCookies() {
        try {
            String backup = fileManager.loadCookiesBackup();
            if (backup == null || backup.isEmpty()) {
                Toast.makeText(this, "没有找到备份文件", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray jsonArray = new JSONArray(backup);
            CookieManager cookieManager = CookieManager.getInstance();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("name");
                String value = obj.getString("value");
                String domain = obj.optString("domain", "current");

                String cookie = name + "=" + value + "; domain=" + domain + "; path=/";
                cookieManager.setCookie("https://" + domain, cookie);
            }

            cookieManager.flush();
            refreshCookieList(searchInput.getText().toString());
            Toast.makeText(this, "导入成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "导入失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}