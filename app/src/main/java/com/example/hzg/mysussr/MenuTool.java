package com.example.hzg.mysussr;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hzg on 2017/4/3.
 */

public class MenuTool {
    Handler mHandler;
    ProgressDialog mdialog;
    Context mContext;
    ConfigTool mConfigTool;

    public void setDownloadBroadcastReceiver(DownloadBroadcastReceiver downloadBroadcastReceiver) {
        this.downloadBroadcastReceiver = downloadBroadcastReceiver;
    }

    DownloadBroadcastReceiver downloadBroadcastReceiver;
    public MenuTool(Context context,Handler handler, ProgressDialog dialog,ConfigTool configTool) {
        mHandler=handler;
        mdialog= dialog;
        mContext=context;
        mConfigTool=configTool;
    }

    public void menuInstall() {
        exec(mConfigTool.getInstallShell(), mHandler);
    }

    public void menuUninstall() {
        exec(mConfigTool.getRemoveShell(), mHandler);
    }

    public void menuInstallBusybox() {
        Utils.installApk(mContext, StartAct.BusyboxInstallPath,"com.example.hzg.mysussr.provider");
    }

    public void exec(String[] removeShell, Handler mHandler) {
        mdialog = ProgressDialog.show(mContext, "脚本执行", "脚本执行中，请稍等........", true, false);
        ShellTool.execShellWithHandler(removeShell, true, true, mHandler);
    }

    public void menuReset() {
        SharedPreferences.Editor editor=mContext.getSharedPreferences("sussr",MODE_PRIVATE).edit();
        editor.putBoolean("isaccept",false);
        editor.apply();
        Intent intent1=new Intent(mContext,StartAct.class);
        mContext.startActivity(intent1);
        ( (Activity)mContext).finish();
    }

    public void menuHlep() {
        if (Build.VERSION.SDK_INT>19) {
            ShellTool.editTextFileWithShellandStream(mContext, StartAct.sussrPath+"/temp" ,"/data/sussr/说明.txt");
        }
        else  FileTool.editTextFileWithStream(mContext,"/data/sussr/说明.txt");
    }

    public void menuEditSetting() {
        if (Build.VERSION.SDK_INT>19) {
            ShellTool.editTextFileWithShellandStream(mContext, StartAct.sussrPath+"/temp" ,"/data/sussr/setting.ini");
        }
        else  FileTool.editTextFileWithStream(mContext,"/data/sussr/setting.ini");
    }

    public void menuUpdate() {
        UpdateTool.checkUpdate((Activity) mContext,new UpdateTool.CheckCallBack() {
            @Override
            public void onSuccess(final UpdateAppInfo updateAppInfo) {
                AlertDialog.Builder updateBuilder = new AlertDialog.Builder(mContext);
                updateBuilder.setTitle("当前版本"+UpdateTool.getAppVersionName(mContext.getApplicationContext()));
                if (updateAppInfo.data.apkVersionCode>UpdateTool.getAppVersionCode(mContext.getApplicationContext())) {

                    updateBuilder.setMessage("有新版本可以更新!\n"+updateAppInfo.data.toString());
                    final String apkFileName=updateAppInfo.data.apkName+updateAppInfo.data.apkVersion+".apk";
                    updateBuilder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //检查目录中有没有更新文件，有就直接安装，没有就使用DownloadManger下载
                            File apkFile=new File(mContext.getExternalFilesDir("apk")+"/"+apkFileName);
                            if (apkFile.exists())
                            {
                                Utils.installApk(mContext,apkFile.getPath(),"com.example.hzg.mysussr.provider");
                            }
                            else {
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateAppInfo.data.apkUrl));
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                request.setTitle(updateAppInfo.data.apkName + updateAppInfo.data.apkVersion);
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
                    updateBuilder.setNeutralButton("暂不更新",null);
                }
                else
                {
                    updateBuilder.setMessage("当前版本已经是最新的了，无需更新");

                }

                updateBuilder.create().show();
            }

            @Override
            public void onError() {

                Toast.makeText(mContext,"访问失败",Toast.LENGTH_LONG).show();

            }


        });
    }

    public void menuUid() {
        final ArrayList<Utils.AppUidMessage> uidData= (ArrayList<Utils.AppUidMessage>) Utils.getUidList(mContext);
        final AlertDialog.Builder uidBuilder=new AlertDialog.Builder(mContext);
        uidBuilder.setTitle("查看uid");
        RecyclerView recyclerView=new RecyclerView(mContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.uid,parent,false);

                return new UidHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                UidHolder uidHolder= (UidHolder) holder;
                uidHolder.uid.setText(uidData.get(position).getUid());
                uidHolder.label.setText(uidData.get(position).getAppName());
                uidHolder.icon.setImageDrawable(uidData.get(position).getAppIcon());
            }

            @Override
            public int getItemCount() {
                return uidData.size();
            }
            class  UidHolder extends  RecyclerView.ViewHolder{
                private TextView label;
                private  TextView uid;
                private ImageView icon;
                public UidHolder(View itemView) {
                    super(itemView);
                    label= (TextView) itemView.findViewById(R.id.uid_label);
                    uid= (TextView) itemView.findViewById(R.id.uid_uid);
                    icon= (ImageView) itemView.findViewById(R.id.uid_icon);
                }
            }
        });
        uidBuilder.setView(recyclerView);
        uidBuilder.setPositiveButton("关闭",null);
        uidBuilder.create().show();
    }
 public  void  release()
 {
     mContext=null;
     mConfigTool=null;
     mHandler=null;
     mdialog=null;
     downloadBroadcastReceiver=null;
 }

}
