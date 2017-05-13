package com.example.hzg.mysussr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.hzg.mysussr.utils.FileTool;
import com.example.hzg.mysussr.utils.ShellTool;


/**
 * Created by HZG on 2016/10/5.
 */

public class StartAct extends AppCompatActivity {
    String Tag = this.getClass().getSimpleName();
    boolean isaccept = false;
    boolean isReplaced = false;
    SharedPreferences preferences;
    public static String REPLACED_KEY = "isReplaced";
    public static String ACCEPT_KEY = "isaccpet";
    public static String SHAREPREF_NAME = "sussr";
    public static final String sussrPath = Environment.getExternalStorageDirectory().getPath() + "/sussr";
    public static final String sussrInstallPath = sussrPath + "/sussr.zip";
    public static final String BusyboxInstallPath = sussrPath + "/busybox.apk";
    private Class startTager = MainActivitynew.class;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.start);
        preferences = getSharedPreferences(SHAREPREF_NAME, MODE_PRIVATE);
        isaccept = preferences.getBoolean(ACCEPT_KEY, false);
        isReplaced = preferences.getBoolean(REPLACED_KEY, false);
        if (!isaccept) {

            if (Build.VERSION.SDK_INT > 21 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View root = getLayoutInflater().inflate(R.layout.accept, null);
            TextView content = (TextView) root.findViewById(R.id.accept_content);
            final RadioButton rbyes = (RadioButton) root.findViewById(R.id.acccept_rb_yes);
            builder.setTitle("使用须知");
            builder.setView(root);
            content.setText("1.软件需要root权限;" +
                    "\n2.软件需要安装busybox;" +
                    "\n3.使用过程出现的任何问题概不负责");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (rbyes.isChecked()) {
                        isaccept = true;
                        FileTool.copyFileFromAssets(StartAct.this, sussrPath);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putBoolean(ACCEPT_KEY, isaccept);
                        edit.apply();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(StartAct.this, startTager));
                                finish();
                            }
                        }, 1000);

                    } else {
                        finish();
                    }
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        } else {
            if (isReplaced) {
                Log.i(Tag, "应用更新，重新配置环境");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileTool.copyFileFromAssets(StartAct.this, sussrPath);
                        ShellTool.execShell(ConfigToolnew.ReInstall_Sussr, true, true);
                    }
                }).start();
                preferences.edit()
                        .putBoolean(REPLACED_KEY, false)
                        .apply();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(StartAct.this, startTager));
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            }

        }
    }


}
