package com.mho.web.wv.mohong.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mho.web.wv.mohong.R;
import com.mho.web.wv.mohong.adapter.TabListAdapter;
import com.mho.web.wv.mohong.manager.TabManager;
import com.mho.web.wv.mohong.model.TabInfo;

import java.util.List;

public class TabBottomSheet extends BottomSheetDialog {
    
    private Context context;
    private TabManager tabManager;
    private TabListAdapter adapter;
    private int currentPosition;
    private OnTabSheetListener listener;
    
    public interface OnTabSheetListener {
        void onTabSelected(int position);
        void onNewTab();
        void onUrlSubmit(String url);
    }
    
    public TabBottomSheet(@NonNull Context context, TabManager tabManager, int currentPosition) {
        super(context);
        this.context = context;
        this.tabManager = tabManager;
        this.currentPosition = currentPosition;
    }
    
    public void setOnTabSheetListener(OnTabSheetListener listener) {
        this.listener = listener;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
        
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(16, 16, 16, 16);
        
        LinearLayout urlLayout = new LinearLayout(context);
        urlLayout.setOrientation(LinearLayout.HORIZONTAL);
        urlLayout.setPadding(0, 0, 0, 16);
        
        EditText urlInput = new EditText(context);
        urlInput.setHint("输入网址");
        urlInput.setSingleLine(true);
        LinearLayout.LayoutParams urlParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        urlParams.setMargins(0, 0, 8, 0);
        urlInput.setLayoutParams(urlParams);
        
        Button goButton = new Button(context);
        goButton.setText("前往");
        goButton.setOnClickListener(v -> {
            String url = urlInput.getText().toString();
            if (listener != null && !url.isEmpty()) {
                listener.onUrlSubmit(url);
                dismiss();
            }
        });
        
        urlLayout.addView(urlInput);
        urlLayout.addView(goButton);
        container.addView(urlLayout);
        
        TextView titleText = new TextView(context);
        titleText.setText("标签页列表");
        titleText.setTextSize(16);
        titleText.setPadding(0, 8, 0, 8);
        container.addView(titleText);
        
        List<TabInfo> tabs = new java.util.ArrayList<>();
        for (int i = 0; i < tabManager.getTabCount(); i++) {
            TabInfo tab = tabManager.getTab(i);
            if (tab != null) {
                tabs.add(tab);
            }
        }
        
        adapter = new TabListAdapter(tabs, currentPosition);
        adapter.setOnTabClickListener(position -> {
            if (listener != null) {
                listener.onTabSelected(position);
                dismiss();
            }
        });
        adapter.setOnTabCloseListener(position -> {
            tabManager.closeTab(position);
            List<TabInfo> newTabs = new java.util.ArrayList<>();
            for (int i = 0; i < tabManager.getTabCount(); i++) {
                newTabs.add(tabManager.getTab(i));
            }
            adapter.updateData(newTabs, Math.min(currentPosition, newTabs.size() - 1));
            if (newTabs.isEmpty()) {
                dismiss();
            }
        });
        
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        container.addView(recyclerView);
        
        Button newTabButton = new Button(context);
        newTabButton.setText("+ 新建标签页");
        newTabButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNewTab();
                dismiss();
            }
        });
        container.addView(newTabButton);
        
        setContentView(container);
    }
}