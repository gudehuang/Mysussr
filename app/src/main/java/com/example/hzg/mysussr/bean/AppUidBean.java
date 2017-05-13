package com.example.hzg.mysussr.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by hzg on 2017/5/9.
 */

public class AppUidBean {
   private String appName;
   private Drawable appIcon;
   private String Uid;
   private  boolean isSelected=false;
    public AppUidBean(String appName, Drawable appIcon, String uid) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.Uid = uid;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public String getUid() {
        return Uid;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
    public AppUidBean clone()
    {
        return new AppUidBean(appName,appIcon,Uid);
    }
}

