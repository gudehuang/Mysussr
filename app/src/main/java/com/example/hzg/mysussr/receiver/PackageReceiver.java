package com.example.hzg.mysussr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hzg.mysussr.StartAct;

/**
 * Created by hzg on 2017/5/12.
 */

public class PackageReceiver extends BroadcastReceiver {
    String Tag=this.getClass().getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        // 安装
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
            System.out.println("android.intent.action.PACKAGE_ADDED---------------" + packageName);
        }
        // 覆盖安装
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            String packageName = intent.getDataString();
            if (packageName.equals("com.example.hzg.mysussr"))
            {
                context.getSharedPreferences(StartAct.SHAREPREF_NAME,Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(StartAct.REPLACED_KEY,true)
                        .apply();
                Log.i(Tag,"应用更新");
            }
            System.out.println("android.intent.action.PACKAGE_REPLACED---------------" + packageName);
        }
        // 移除
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString();
            System.out.println("android.intent.action.PACKAGE_REMOVED---------------" + packageName);
        }
    }
}
