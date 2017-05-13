package com.example.hzg.mysussr.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by hzg on 2017/5/10.
 */

public class MyViewPager extends ViewPager {
    private  boolean NoScroll=true;
     public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (NoScroll)
            return false;
        else
        return super.onTouchEvent(ev);
    }

    public void setNoScroll(boolean noScroll) {
        NoScroll = noScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //截取事件，避免左右滑动TabTab
        return false;
    }
}
