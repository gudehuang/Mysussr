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
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;



/**
 * Created by HZG on 2016/10/5.
 */

public class StartAct extends AppCompatActivity {
    boolean isaccept=false;
    SharedPreferences preferences;
     public static final  String sussrPath=Environment.getExternalStorageDirectory().getPath()+"/sussr";
     public static final  String sussrInstallPath=sussrPath+"/sussr.zip";
     public static final  String BusyboxInstallPath=sussrPath+"/busybox.apk";
  private  Class  startTager=MainActivityUstTask.class ;
  //private  Class  startTager=MainActivity1.class ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.start);
        preferences=getSharedPreferences("sussr",MODE_PRIVATE);
        isaccept=preferences.getBoolean("isaccept",false);

        if (!isaccept)
        {

            if (Build.VERSION.SDK_INT>21&&ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
            AlertDialog.Builder  builder=new AlertDialog.Builder(this);
            View root=getLayoutInflater().inflate(R.layout.accept,null);
            TextView content= (TextView) root.findViewById(R.id.accept_content);
            final RadioButton rbyes= (RadioButton) root.findViewById(R.id.acccept_rb_yes);
            RadioButton rbno= (RadioButton) root.findViewById(R.id.acccept_rb_no);
            builder.setTitle("使用须知");
            builder.setView(root);
           content.setText("1.软件需要root权限;" +
                    "\n2.软件需要安装busybox;" +
                    "\n3.使用过程出现的任何问题概不负责");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (rbyes.isChecked())
                    {
                        isaccept=true;
                        FileTool.copyFileFromAssets(StartAct.this,sussrPath);
                        SharedPreferences.Editor edit=preferences.edit();
                        edit.putBoolean("isaccept",isaccept);
                        edit.commit();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(StartAct.this,startTager));
                                finish();
                            }
                        },1000);

                    }
                    else {
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
        }
        else
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(StartAct.this,startTager));
                    finish();
                }
            },1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1)
        {
            if (grantResults[0]==PackageManager.PERMISSION_DENIED)
            {
              finish();
            }

        }
    }




}
