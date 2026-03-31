package com.mho.web.wv.mohong;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mho.web.wv.mohong.manager.SettingsManager;

public class ThemePickerActivity extends AppCompatActivity {
    
    private SettingsManager settingsManager;
    private int currentColor;
    
    // 预设主题色
    private int[] presetColors = {
        0xFF2196F3, // 蓝色
        0xFFF44336, // 红色
        0xFF4CAF50, // 绿色
        0xFFFF9800, // 橙色
        0xFF9C27B0, // 紫色
        0xFFFFC107, // 琥珀色
        0xFFE91E63, // 粉色
        0xFF00BCD4, // 青色
        0xFF795548, // 棕色
        0xFF607D8B  // 蓝灰色
    };
    
    private String[] presetNames = {
        "蓝色", "红色", "绿色", "橙色", "紫色",
        "琥珀", "粉色", "青色", "棕色", "蓝灰"
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        settingsManager = SettingsManager.getInstance(this);
        currentColor = settingsManager.getThemeRgb();
        
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 48, 32, 32);
        
        TextView title = new TextView(this);
        title.setText("选择主题颜色");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 32);
        layout.addView(title);
        
        // 当前颜色显示
        LinearLayout currentLayout = new LinearLayout(this);
        currentLayout.setOrientation(LinearLayout.HORIZONTAL);
        currentLayout.setPadding(0, 0, 0, 32);
        
        TextView currentLabel = new TextView(this);
        currentLabel.setText("当前颜色: ");
        currentLabel.setTextSize(16);
        
        View colorPreview = new View(this);
        LinearLayout.LayoutParams previewParams = new LinearLayout.LayoutParams(80, 80);
        previewParams.setMargins(16, 0, 0, 0);
        colorPreview.setLayoutParams(previewParams);
        colorPreview.setBackgroundColor(currentColor);
        
        currentLayout.addView(currentLabel);
        currentLayout.addView(colorPreview);
        layout.addView(currentLayout);
        
        // 预设颜色区域
        TextView presetTitle = new TextView(this);
        presetTitle.setText("预设颜色");
        presetTitle.setTextSize(18);
        presetTitle.setPadding(0, 16, 0, 16);
        layout.addView(presetTitle);
        
        // 创建预设颜色网格
        LinearLayout colorGrid = new LinearLayout(this);
        colorGrid.setOrientation(LinearLayout.VERTICAL);
        
        int cols = 2;
        for (int i = 0; i < presetColors.length; i += cols) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 0, 0, 16);
            
            for (int j = 0; j < cols && i + j < presetColors.length; j++) {
                int index = i + j;
                LinearLayout colorItem = new LinearLayout(this);
                colorItem.setOrientation(LinearLayout.VERTICAL);
                colorItem.setPadding(8, 8, 8, 8);
                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                itemParams.setMargins(8, 0, 8, 0);
                colorItem.setLayoutParams(itemParams);
                
                View colorView = new View(this);
                LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 120);
                colorView.setLayoutParams(colorParams);
                colorView.setBackgroundColor(presetColors[index]);
                
                TextView colorName = new TextView(this);
                colorName.setText(presetNames[index]);
                colorName.setTextSize(12);
                colorName.setGravity(android.view.Gravity.CENTER);
                colorName.setPadding(0, 8, 0, 0);
                
                colorItem.addView(colorView);
                colorItem.addView(colorName);
                
                final int color = presetColors[index];
                colorItem.setOnClickListener(v -> {
                    currentColor = color;
                    colorPreview.setBackgroundColor(currentColor);
                    settingsManager.setThemeRgb(currentColor);
                });
                
                row.addView(colorItem);
            }
            colorGrid.addView(row);
        }
        layout.addView(colorGrid);
        
        // RGB 自定义颜色选择器标题
        TextView customTitle = new TextView(this);
        customTitle.setText("RGB 自定义颜色");
        customTitle.setTextSize(18);
        customTitle.setPadding(0, 32, 0, 16);
        layout.addView(customTitle);
        
        // 红色调节
        LinearLayout redLayout = createColorSlider("红色", Color.red(currentColor), 255);
        layout.addView(redLayout);
        
        // 绿色调节
        LinearLayout greenLayout = createColorSlider("绿色", Color.green(currentColor), 255);
        layout.addView(greenLayout);
        
        // 蓝色调节
        LinearLayout blueLayout = createColorSlider("蓝色", Color.blue(currentColor), 255);
        layout.addView(blueLayout);
        
        // 颜色预览
        LinearLayout previewLayout = new LinearLayout(this);
        previewLayout.setOrientation(LinearLayout.HORIZONTAL);
        previewLayout.setPadding(0, 32, 0, 32);
        
        TextView previewLabel = new TextView(this);
        previewLabel.setText("最终颜色: ");
        previewLabel.setTextSize(16);
        
        View finalPreview = new View(this);
        LinearLayout.LayoutParams finalPreviewParams = new LinearLayout.LayoutParams(100, 80);
        finalPreviewParams.setMargins(16, 0, 0, 0);
        finalPreview.setLayoutParams(finalPreviewParams);
        finalPreview.setBackgroundColor(currentColor);
        
        previewLayout.addView(previewLabel);
        previewLayout.addView(finalPreview);
        layout.addView(previewLayout);
        
        // RGB值显示
        TextView rgbText = new TextView(this);
        rgbText.setText(String.format("RGB(%d, %d, %d)", 
                Color.red(currentColor), Color.green(currentColor), Color.blue(currentColor)));
        rgbText.setTextSize(12);
        layout.addView(rgbText);
        
        // 完成按钮
        Button doneButton = new Button(this);
        doneButton.setText("应用并返回");
        doneButton.setOnClickListener(v -> {
            settingsManager.setThemeRgb(currentColor);
            finish();
        });
        layout.addView(doneButton);
        
        scrollView.addView(layout);
        setContentView(scrollView);
        
        // 存储滑块引用以便更新
        final SeekBar[] redSeek = new SeekBar[1];
        final SeekBar[] greenSeek = new SeekBar[1];
        final SeekBar[] blueSeek = new SeekBar[1];
        
        // 需要重新获取这些滑块
    }
    
    private LinearLayout createColorSlider(String name, int initialValue, int max) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 8, 0, 8);
        
        TextView label = new TextView(this);
        label.setText(name + ": " + initialValue);
        label.setTextSize(14);
        
        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(max);
        seekBar.setProgress(initialValue);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                label.setText(name + ": " + progress);
                if (fromUser) {
                    updateCustomColor();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        layout.addView(label);
        layout.addView(seekBar);
        
        return layout;
    }
    
    private void updateCustomColor() {
        // 需要在完整实现中获取所有滑块值
    }
}