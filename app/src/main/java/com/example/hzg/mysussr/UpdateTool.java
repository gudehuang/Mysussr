package com.example.hzg.mysussr;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hzg on 2017/3/19.
 */

public class UpdateTool {
    /**
     * Created by hzg on 2017/3/20.
     */

    public interface HttpConnectCallback {
        void onSuccess(String result);
        void  onError();
    }

    /**
     * Created by hzg on 2017/3/19.
     */

    public  interface CheckCallBack {
        void onSuccess(UpdateAppInfo updateAppInfo);
        void onError();
    }
     static  String mUrl="http://123.207.107.156:80/mysussr/update.json";
    static Activity mAct;

    public static void setmAct(Activity mAct) {
        UpdateTool.mAct = mAct;
    }

    public static int getAppVersionCode(Context context) {

       int versioncode = -1;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = pi.versionCode;

        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode;
    }
    public static String getAppVersionName(Context context) {

       String versionname="";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionname = pi.versionName;

        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionname;
    }
    public  static void checkUpdate (final CheckCallBack checkCallBack)
    {

        get(mUrl, new HttpConnectCallback() {
            @Override
            public void onSuccess(String result) {
                    Gson gson=new Gson();
                UpdateAppInfo updateAppInfo=null;
                try {
                    updateAppInfo=gson.fromJson(result,UpdateAppInfo.class);
                    checkCallBack.onSuccess(updateAppInfo);
                }catch (Exception e)
                {
                    checkCallBack.onError();
                }


            }

            @Override
            public void onError() {
               checkCallBack.onError();
            }
        });


    }

    public static void get(final String urlpath, final HttpConnectCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                boolean isSucess=false;
                try {
                    URL url=new URL(urlpath);
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(10000);
                    int responseCode=connection.getResponseCode();
                    if (responseCode==200)
                    {
                        InputStream is=connection.getInputStream();
                        final String result=getStringFormInputStream(is);
                        System.out.println(result);
                        isSucess=true;
                       mAct.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               callback.onSuccess(result);
                           }
                       });

                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();

                }finally {
                    if (connection!=null)
                        connection.disconnect();
                    if (!isSucess)
                        mAct.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError();
                            }
                        });
                }
            }
        }).start();

    }

    private static String getStringFormInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        byte[] bytes=new byte[1024];
        int len=-1;
        while ((len=is.read(bytes))!=-1)
        {
            baos.write(bytes,0,len);
        }
        is.close();
        String result=baos.toString("GBK");
        baos.close();
        return  result;
    }

//    private static void usexUtils(final CheckCallBack checkCallBack) {
//        RequestParams params=new RequestParams("http://123.207.107.156:80/mysussr/update.json");
//        // RequestParams params=new RequestParams("http://baidu.com");
//        // params.addQueryStringParameter("wd", "xUtils");
//        params.setCharset("GBK");
//        x.http().get(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                Gson gson=new Gson();
//                UpdateAppInfo updateAppInfo=gson.fromJson(result,UpdateAppInfo.class);
//                checkCallBack.onSuccess(updateAppInfo);
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//          checkCallBack.onError(ex);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//    }



}
