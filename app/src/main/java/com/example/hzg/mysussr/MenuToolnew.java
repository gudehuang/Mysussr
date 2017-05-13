package com.example.hzg.mysussr;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.hzg.mysussr.application.MyApplication;
import com.example.hzg.mysussr.bean.AppUpdateBean;
import com.example.hzg.mysussr.fragment.UidActivity;
import com.example.hzg.mysussr.receiver.DownloadBroadcastReceiver;
import com.example.hzg.mysussr.utils.FileTool;
import com.example.hzg.mysussr.utils.ShellTool;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hzg on 2017/4/3.
 */

public class MenuToolnew {
    Handler mHandler;
    ProgressDialog mdialog;
    Context mContext;
    ConfigToolnew mConfigTool;

    public void setDownloadBroadcastReceiver(DownloadBroadcastReceiver downloadBroadcastReceiver) {
        this.downloadBroadcastReceiver = downloadBroadcastReceiver;
    }

    DownloadBroadcastReceiver downloadBroadcastReceiver;

    public MenuToolnew(Context mContext, ConfigToolnew mConfigTool, DownloadBroadcastReceiver downloadBroadcastReceiver) {
        this.mContext = mContext;
        this.mConfigTool = mConfigTool;
        this.downloadBroadcastReceiver = downloadBroadcastReceiver;
    }

    public MenuToolnew(Context context, Handler handler, ProgressDialog dialog, ConfigToolnew configTool) {
        mHandler = handler;
        mdialog = dialog;
        mContext = context;
        mConfigTool = configTool;
    }

    public void menuInstall() {
        ShellTool.execShellTask(mContext, mConfigTool.getInstallShell(), true, true);

    }

    public void menuUninstall() {
        ShellTool.execShellTask(mContext, mConfigTool.getRemoveShell(), true, true);

    }

    public void menuInstallBusybox() {
        Utils.installApk(mContext, StartAct.BusyboxInstallPath, "com.example.hzg.mysussr.provider");
    }


    public void menuReset() {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("sussr", MODE_PRIVATE).edit();
        editor.putBoolean("isaccept", false);
        editor.apply();
        Intent intent1 = new Intent(mContext, StartAct.class);
        mContext.startActivity(intent1);
        ((Activity) mContext).finish();
    }

    public void menuReLoad(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(context, "正在重写文件", "正在重写文件,请稍等", false, false);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.dismiss();
                Toast.makeText(context, "重写完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                FileTool.copyFileFromAssets(context, StartAct.sussrPath);
                return null;
            }
        }.execute();

    }

    public void menuHlep() {
        if (Build.VERSION.SDK_INT > 19) {
            ShellTool.editTextFileWithShellandStream(mContext, StartAct.sussrPath + "/temp", "/data/sussr/说明.txt");
        } else FileTool.editTextFileWithStream(mContext, "/data/sussr/说明.txt");
    }

    public void menuEditSetting() {
        if (Build.VERSION.SDK_INT > 19) {
            ShellTool.editTextFileWithShellandStream(mContext, StartAct.sussrPath + "/temp", "/data/sussr/setting.ini");
        } else FileTool.editTextFileWithStream(mContext, "/data/sussr/setting.ini");
    }

    public void menuUpdate() {
        UpdateTool.checkUpdate(new UpdateTool.CheckCallBack() {
            @Override
            public void onSuccess(final AppUpdateBean appUpdateBean) {
                AlertDialog.Builder updateBuilder = new AlertDialog.Builder(mContext);
                updateBuilder.setTitle("当前版本" + UpdateTool.getAppVersionName(mContext.getApplicationContext()));
                if (appUpdateBean.data.apkVersionCode > UpdateTool.getAppVersionCode(mContext.getApplicationContext())) {

                    updateBuilder.setMessage("有新版本可以更新!\n" + appUpdateBean.data.toString());
                    final String apkFileName = appUpdateBean.data.apkName + appUpdateBean.data.apkVersion + ".apk";
                    updateBuilder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //检查目录中有没有更新文件，有就直接安装，没有就使用DownloadManger下载
                            File apkFile = new File(mContext.getExternalFilesDir("apk") + "/" + apkFileName);
                            if (apkFile.exists()) {
                                Utils.installApk(mContext, apkFile.getPath(), "com.example.hzg.mysussr.provider");
                            } else {
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(appUpdateBean.data.apkUrl));
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                request.setTitle(appUpdateBean.data.apkName + appUpdateBean.data.apkVersion);
                                request.setDescription("MySussr更新中");
                                request.setMimeType("application/vnd.android.package-archive");
                                request.setDestinationInExternalFilesDir(mContext, "apk", apkFileName);
                                //DownloadManger 下载的文件重名不会覆写，只会在文件名后加一些标识符
                                // 如 update-1.apk ，-2
                                //需要清理重复的文件
                                File apkdir = mContext.getExternalFilesDir("apk");
                                if (apkdir.exists()) {
                                    File[] files = apkdir.listFiles();
                                    for (File file : files) {
                                        file.delete();
                                    }
                                }
                                DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
                                long id = downloadManager.enqueue(request);
                                downloadBroadcastReceiver.setmDownloadId(id);
                            }
                        }
                    });
                    updateBuilder.setNeutralButton("暂不更新", null);
                } else {
                    updateBuilder.setMessage("当前版本已经是最新的了，无需更新");

                }

                updateBuilder.create().show();
            }

            @Override
            public void onError() {

                Toast.makeText(mContext, "访问失败", Toast.LENGTH_LONG).show();

            }


        });
    }

    public void menuUid(Context context) {
        context.startActivity(new Intent(context, UidActivity.class));
    }

    public void menuUidList(Context context, FragmentManager manager) {
        ((MyApplication) mContext.getApplicationContext()).setAppUidList(com.example.hzg.mysussr.utils.Utils.getAppUidList(mContext));

        context.startActivity(new Intent(context, UidActivity.class));

    }

    public void release() {
        mContext = null;
        mConfigTool = null;
        mHandler = null;
        mdialog = null;
        downloadBroadcastReceiver = null;
    }

}
