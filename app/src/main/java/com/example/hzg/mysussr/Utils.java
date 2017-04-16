package com.example.hzg.mysussr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzg on 2017/3/20.
 */

public class Utils {
    /***安装apk
     *
     * @param context
     * @param apkPath            apk文件地址
     * @param providerAuthority  FileProvider标识，android7.0 以上需要用到
     *
     */
    public static void installApk(Context context, String apkPath, @Nullable String providerAuthority) {
        Intent intent=new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT>23) {
            if (providerAuthority==null)return;
            Uri apkUri = FileProvider.getUriForFile(context, providerAuthority, new File(apkPath));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }
        else intent.setDataAndType(Uri.fromFile(new File(apkPath)),"application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public  static  List getUidList(Context context) {
        PackageManager pm=context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<AppUidMessage> uidMessageList = new ArrayList<>();
        for (int i = 0; i < packageInfos.size(); i++) {
            if ((packageInfos.get(i).applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)<=0) {
                String label = (String) packageInfos.get(i).applicationInfo.loadLabel(pm);
                Drawable icon = packageInfos.get(i).applicationInfo.loadIcon(pm);
                String uid = String.valueOf(packageInfos.get(i).applicationInfo.uid);
                uidMessageList.add(new AppUidMessage(label, icon, uid));
            }
        }
        return  uidMessageList;
    }
    public  static  void  closed(Closeable closeable)
    {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   public static class  AppUidMessage{
        String appName;
        Drawable appIcon;
        String Uid;

        public AppUidMessage(String appName, Drawable appIcon, String uid) {
            this.appName = appName;
            this.appIcon = appIcon;
            Uid = uid;
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
    }

}
