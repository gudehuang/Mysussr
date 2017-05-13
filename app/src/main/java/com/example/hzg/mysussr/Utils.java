package com.example.hzg.mysussr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.TypedValue;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Created by hzg on 2017/3/20.
 *
 */

public class Utils {
    /***安装apk
     *
     * @param context             上下问
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

   public   static  void  closed(Closeable closeable)
    {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   public static  float  dp2px(Context context,float value)
    {
     return   TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,context.getResources().getDisplayMetrics());

    }
    public  static int px2dp(Context context,float value)
    {
        float scale=context.getResources().getDisplayMetrics().density;
        return (int) (value/scale+0.5f);
    }


}
