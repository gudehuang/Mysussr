package com.example.hzg.mysussr.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.hzg.mysussr.Utils;

/**
 * Created by hzg on 2017/2/8.
 */

public class DownloadBroadcastReceiver extends BroadcastReceiver{
    private  long mDownloadId;
    @Override
    public void onReceive(Context context, Intent intent) {

        long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (completeDownloadId==mDownloadId) {
            DownloadManager downManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadFileUri = downManager.getUriForDownloadedFile(completeDownloadId);
            System.out.println(downloadFileUri);
            // 在android7.0上 uri为content://downloads/all_downloads/12
            //在4.4上       uri为://file:......
            Cursor c = downManager.query(new DownloadManager.Query().setFilterById(completeDownloadId));
            if (c!=null) {
                c.moveToFirst();
                String path = Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).getPath();
                System.out.println(path);
                c.close();
                Utils.installApk(context, path, "com.example.hzg.mysussr.provider");
            }

            }



    }

    public void setmDownloadId(long mDownloadId) {
        this.mDownloadId = mDownloadId;
    }
}
