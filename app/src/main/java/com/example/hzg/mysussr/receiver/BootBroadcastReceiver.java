package com.example.hzg.mysussr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hzg.mysussr.ConfigToolnew;
import com.example.hzg.mysussr.utils.ShellTool;

/**
 * Created by hzg on 2017/2/8.
 */

public class BootBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

    boolean boot=context.getSharedPreferences("sussr",Context.MODE_PRIVATE).getBoolean("boot",false);
        Log.d("Boot","开机脚本自启："+boot);
        if (boot)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ShellTool.execShell(new String[]{ConfigToolnew.StartSussrShell},true,false);
                }
            }).start();
        }

    }
}
