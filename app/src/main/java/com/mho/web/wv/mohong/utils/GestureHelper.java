package com.mho.web.wv.mohong.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class GestureHelper {
    
    private Context context;
    private OnGestureListener listener;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;
    
    private float startX, startY;
    private boolean isTwoFinger = false;
    
    public interface OnGestureListener {
        void onSwipeLeft();
        void onSwipeRight();
        void onTwoFingerSwipeUp();
        void onTwoFingerSwipeDown();
    }
    
    public GestureHelper(Context context, OnGestureListener listener) {
        this.context = context;
        this.listener = listener;
        
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (e1 == null) return false;
                
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                
                int pointerCount = e2.getPointerCount();
                
                if (pointerCount == 1) {
                    if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 100) {
                        if (diffX > 0 && e1.getX() < 100) {
                            listener.onSwipeRight();
                            return true;
                        } else if (diffX < 0 && e1.getX() > context.getResources().getDisplayMetrics().widthPixels - 100) {
                            listener.onSwipeLeft();
                            return true;
                        }
                    }
                } else if (pointerCount == 2) {
                    if (Math.abs(diffY) > 100) {
                        if (diffY < 0) {
                            listener.onTwoFingerSwipeUp();
                        } else if (diffY > 0) {
                            listener.onTwoFingerSwipeDown();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        
        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener());
    }
    
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        scaleDetector.onTouchEvent(event);
        return true;
    }
}