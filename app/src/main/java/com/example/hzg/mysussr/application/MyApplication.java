package com.example.hzg.mysussr.application;

import android.app.Application;

import com.example.hzg.mysussr.bean.AppUidBean;

import java.util.ArrayList;


/**
 * Created by hzg on 2017/3/19.
 *
 */

public class MyApplication extends Application{
    private ArrayList<AppUidBean> mAppUidList;
    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void setAppUidList(ArrayList<AppUidBean> appUidList) {
        this.mAppUidList = appUidList;
    }

    public ArrayList<AppUidBean> getAppUidList() {
        return mAppUidList;
    }
}
