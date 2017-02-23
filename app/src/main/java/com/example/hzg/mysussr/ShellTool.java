package com.example.hzg.mysussr;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


/**
 * Created by hzg on 2016/12/20.
 */

public class ShellTool {
  public  static final  int  EXEC_SHELL_HANDLER=0x123;
    /**
     *
     * @param cmd    shell命令数组
     * @param isroot  是否使用root权限
     * @return  String[],String[0]为输出信息，String[1]为错误信息
     */
    public static  String[] execShell(String[] cmd, boolean isroot,boolean isgeterrmessage)
    {
        System.out.println(cmd);
        Process p=null;
        StringBuffer errMessage =new StringBuffer();
        StringBuffer  message = new StringBuffer();
        try {
            if (isroot)
                p = Runtime.getRuntime().exec("su");
            else {
                p = Runtime.getRuntime().exec(cmd[0]);
            }

            OutputStream os=p.getOutputStream();
            InputStream is=p.getInputStream();
            InputStream err=p.getErrorStream();
            for (String line:cmd)
            {
                Log.d("ShellTool#cmd",line);
                os.write((line+"\n").getBytes());
            }
            os.close();
            BufferedReader read=new BufferedReader(new InputStreamReader(is));
            String line=null;
            while((line=read.readLine())!=null)
            {
                Log.d("ShellTool#outputText",line);
                message.append(line+"\n");
            }
            if (isgeterrmessage) {
                BufferedReader read_err = new BufferedReader(new InputStreamReader(err));
                while ((line = read_err.readLine()) != null) {
                    Log.d("ShellTool#errText",line);
                    errMessage.append(line + "\n");
                }
                err.close();
            }
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
            errMessage.append(e.getMessage());
        }
        return new String[]{message.toString(),errMessage.toString()};

    }
    public static  void editTextFileWithShell(final Context context, final String path) {
        String result=null;
        result=ShellTool.execShell(new String[]{"cat "+path},true,true)[0];
        Log.d("readTextFile","result="+result);
        if (result!=null&&result!="") {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(path+"(shell模式,文本里不能有')");
            final EditText editText1 = new EditText(context);
            editText1.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            editText1.setText(result);
            builder.setView(editText1);
            builder.setPositiveButton("确定修改", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ShellTool.execShell(new String[]{"echo \'"+editText1.getText().toString()+"\' "+"> /data/sussr/setting.ini"},true,true);
                }
            });
            builder.setNegativeButton("放弃修改", null);
            builder.show();
        }

    }
    public static  void editTextFileWithShellandStream(final Context context,final  String tempPath, final String path) {
        String result=null;
        result=ShellTool.execShell(new String[]{"cat "+path},true,true)[0];
        Log.d("readTextFile","result="+result);
        if (result!=null&&result!="") {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(path+"(shell模式)");
            final EditText editText1 = new EditText(context);
            editText1.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            editText1.setText(result);
            builder.setView(editText1);
            builder.setPositiveButton("确定修改", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        OutputStream os = new FileOutputStream(tempPath);
                        os.write((editText1.getText().toString()).getBytes());
                        os.close();
                        ShellTool.execShell(new String[]{"cat "+tempPath+" > /data/sussr/setting.ini"},true,true);
                    } catch (FileNotFoundException e) {

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
            builder.setNegativeButton("放弃修改", null);
            builder.show();
        }

    }
    public static  void execShellWithHandler(final String[] cmd, final boolean isroot, final boolean isgeterrmessage, final Handler handler)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] result=execShell(cmd,isroot,isgeterrmessage);
                Message message=Message.obtain();
                message.what=EXEC_SHELL_HANDLER;
                message.obj=result;
                handler.sendMessage(message);
            }
        }).start();

    }
}
