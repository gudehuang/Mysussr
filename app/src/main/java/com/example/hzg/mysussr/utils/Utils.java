package com.example.hzg.mysussr.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.example.hzg.mysussr.application.MyApplication;
import com.example.hzg.mysussr.bean.AppUidBean;
import com.example.hzg.mysussr.bean.ConfigListBean;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hzg on 2017/5/9.
 *
 */

public class Utils {
    public static ConfigListBean getConfigList(String json) {
        ConfigListBean configListBean ;
        Gson gson = new Gson();
        configListBean = gson.fromJson(json, ConfigListBean.class);
        return configListBean;
    }

    /**
     * 从PackageManage获取已安装软件的UID信息（不包括系统软件）
     *
     * @param context context
     * @return 装载应用UID信息的列表
     */
    public static ArrayList<AppUidBean> getAppUidList(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        ArrayList<AppUidBean> appUidBeanList = new ArrayList<>();
        for (int i = 0; i < packageInfos.size(); i++) {
            if ((packageInfos.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                String label = (String) packageInfos.get(i).applicationInfo.loadLabel(pm);
                Drawable icon = packageInfos.get(i).applicationInfo.loadIcon(pm);
                String uid = String.valueOf(packageInfos.get(i).applicationInfo.uid);
                appUidBeanList.add(new AppUidBean(label, icon, uid));
            }
        }
        return appUidBeanList;
    }

    public static ArrayList<String> getSelectedList(Context context, String type) {
        ArrayList<String> result = new ArrayList<>();
        String selectString = context.getSharedPreferences("sussr", Context.MODE_PRIVATE)
                .getString(type, null);
        if (selectString != null) {
            String[] sqlit = selectString.split(",");
             result.addAll(Arrays.asList(sqlit));
        }
        return result;
    }

    public static String arrayToString(List<String> arrry, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arrry.size(); i++) {
            Object object = arrry.get(i);
            builder.append(object);
            if (i < arrry.size() - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    public static ArrayList<String> stringToArray(String src, String separator) {
        ArrayList<String> arrayList =new ArrayList<>();
        String[] strings = src.split(separator);
        if (strings.length>0) {
          arrayList.addAll(Arrays.asList(strings));
        }
        return arrayList;
    }

    public static String getStringFromSharePre(Context context, String key) {

        return context.getSharedPreferences("sussr", Context.MODE_PRIVATE)
                .getString(key, "");
    }

    public static void initUidMsg(Context context) {
        ((MyApplication) context.getApplicationContext())
                .setAppUidList(getAppUidList(context));
    }
}
