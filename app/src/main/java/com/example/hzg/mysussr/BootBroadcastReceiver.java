package com.example.hzg.mysussr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
                    ShellTool.execShell(new String[]{ConfigTool.StartSussrShell},true,false);
                }
            }).start();
        }

    }
}
