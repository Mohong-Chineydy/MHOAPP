package com.mho.web.wv.mohong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mho.web.wv.mohong.R;
import com.mho.web.wv.mohong.model.TabInfo;

import java.util.List;

public class TabListAdapter extends RecyclerView.Adapter<TabListAdapter.TabViewHolder> {
    
    private List<TabInfo> tabs;
    private int currentPosition;
    private OnTabClickListener tabClickListener;
    private OnTabCloseListener closeListener;
    
    public interface OnTabClickListener {
        void onTabClick(int position);
    }
    
    public interface OnTabCloseListener {
        void onTabClose(int position);
    }
    
    public TabListAdapter(List<TabInfo> tabs, int currentPosition) {
        this.tabs = tabs;
        this.currentPosition = currentPosition;
    }
    
    public void setOnTabClickListener(OnTabClickListener listener) {
        this.tabClickListener = listener;
    }
    
    public void setOnTabCloseListener(OnTabCloseListener listener) {
        this.closeListener = listener;
    }
    
    public void updateData(List<TabInfo> newTabs, int newPosition) {
        this.tabs = newTabs;
        this.currentPosition = newPosition;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new TabViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {
        TabInfo tab = tabs.get(position);
        
        String title = tab.getTitle();
        if (title == null || title.isEmpty()) {
            title = "新标签页";
        }
        if (title.length() > 30) {
            title = title.substring(0, 27) + "...";
        }
        
        holder.text1.setText(title);
        holder.text2.setText(tab.getUrl());
        
        if (position == currentPosition) {
            holder.itemView.setBackgroundColor(0x1F2196F3);
        } else {
            holder.itemView.setBackgroundColor(0x00000000);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (tabClickListener != null) {
                tabClickListener.onTabClick(position);
            }
        });
        
        holder.closeButton.setOnClickListener(v -> {
            if (closeListener != null) {
                closeListener.onTabClose(position);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return tabs.size();
    }
    
    public static class TabViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;
        ImageView closeButton;
        
        public TabViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
            
            closeButton = new ImageView(itemView.getContext());
            closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            closeButton.setPadding(16, 16, 16, 16);
            
            if (itemView instanceof ViewGroup) {
                ((ViewGroup) itemView).addView(closeButton);
            }
        }
    }
}