package com.example.hzg.mysussr;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.example.hzg.mysussr.bean.AppUpdateBean;
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

        void onError();
    }

    /**
     * Created by hzg on 2017/3/19.
     */

    public interface CheckCallBack {
        void onSuccess(AppUpdateBean appUpdateBean);

        void onError();
    }

    static String mUrl = "http://123.207.107.156:80/mysussr/update.json";


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

        String versionname = "";
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

    /**
     * 使用AsyncTask 进行网络查询
     *
     * @param checkCallBack 客户端的检查回调
     */
    public static void checkUpdate(CheckCallBack checkCallBack) {
        UpdateTask task = new UpdateTask(mUrl, checkCallBack);
        task.execute();
    }

    /***
     * 使用线程进行网络查询，
     * @deprecated 逻辑不清晰；持有Actvivity引用，容易造成内存泄露；使用checkUpdate(CheckCallBack checkCallBack)
     * @param mact         使用Activity的runOnUiThread( )执行客户端的回调
     * @param checkCallBack 客户端的检查回调
     */
    public static void checkUpdate(final Activity mact, final CheckCallBack checkCallBack) {

        get(mUrl, mact, new HttpConnectCallback() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                AppUpdateBean appUpdateBean = null;
                try {
                    appUpdateBean = gson.fromJson(result, AppUpdateBean.class);
                    checkCallBack.onSuccess(appUpdateBean);
                } catch (Exception e) {
                    checkCallBack.onError();
                }


            }

            @Override
            public void onError() {
                checkCallBack.onError();
            }
        });


    }

    /**
     * 使用线程进行网络查询，
     *
     * @param urlpath  访问网址
     * @param mAct     回调在Ui线程执行
     * @param callback 自己写的联网回调
     */
    public static void get(final String urlpath, final Activity mAct, final HttpConnectCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                boolean isSucess = false;
                try {
                    URL url = new URL(urlpath);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(10000);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream is = connection.getInputStream();
                        final String result = getStringFormInputStream(is);
                        System.out.println(result);
                        isSucess = true;
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

                } finally {
                    if (connection != null)
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

    /**
     * 读取InputStreamd的内容
     *
     * @param is
     * @return 字符串
     * @throws IOException
     */
    private static String getStringFormInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len = -1;
        while ((len = is.read(bytes)) != -1) {
            baos.write(bytes, 0, len);
        }
        String result = baos.toString("GBK");
        baos.close();
        return result;
    }

    /**
     * 升级查询UpdateTask
     */
    static class UpdateTask extends AsyncTask<Void, Void, String> {
        private String urlpath;
        private CheckCallBack checkCallBack;

        public UpdateTask(String urlpath, CheckCallBack checkCallBack) {
            this.urlpath = urlpath;
            this.checkCallBack = checkCallBack;
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection connection = null;
            String result = null;
            try {
                URL url = new URL(urlpath);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(10000);
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    InputStream is = connection.getInputStream();
                    result = getStringFormInputStream(is);
                    is.close();
                    connection.disconnect();
                    System.out.println(result);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                checkCallBack.onError();
            } else {
                Gson gson = new Gson();
                AppUpdateBean appUpdateBean = null;
                try {
                    appUpdateBean = gson.fromJson(s, AppUpdateBean.class);
                    checkCallBack.onSuccess(appUpdateBean);
                } catch (Exception e) {
                    checkCallBack.onError();
                }

            }
        }
    }


}
