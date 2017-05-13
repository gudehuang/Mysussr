package com.example.hzg.mysussr.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


/**
 * Created by hzg on 2016/12/20.
 * 执行shell命令的工具类
 */


public class ShellTool {

    /**
     * @param cmd    shell命令数组
     * @param isroot 是否使用root权限
     * @return String[], String[0]为输出信息，String[1]为错误信息
     */
    public static String[] execShell(String[] cmd, boolean isroot, boolean isgeterrmessage) {
        Process p = null;
        StringBuilder errMessage = new StringBuilder();
        StringBuilder message = new StringBuilder();
        OutputStream os = null;
        InputStream is = null;
        InputStream err = null;
        try {
            if (isroot)
                p = Runtime.getRuntime().exec("su");
            else {
                p = Runtime.getRuntime().exec(cmd[0]);
            }

            os = p.getOutputStream();
            is = p.getInputStream();
            err = p.getErrorStream();
            for (String line : cmd) {
                Log.d("ShellTool#cmd", line);
                os.write((line + "\n").getBytes());
            }
            os.flush();
            os.close();
            os = null;
            BufferedReader read = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = read.readLine()) != null) {
                Log.d("ShellTool#outputText", line);
                message.append(line);
                message.append("\n");
            }
            is.close();
            is = null;
            read.close();
            if (isgeterrmessage) {
                BufferedReader read_err = new BufferedReader(new InputStreamReader(err));
                while ((line = read_err.readLine()) != null) {
                    Log.d("ShellTool#errText", line);
                    errMessage.append(line);
                    errMessage.append("\n");
                }
                err.close();
                err = null;
                read_err.close();
            } else {
                err.close();
                err = null;
            }


        } catch (IOException e) {
            e.printStackTrace();
            errMessage.append(e.getMessage());
        } finally {
            if (p != null)
                p.destroy();
            if (os != null) com.example.hzg.mysussr.Utils.closed(os);
            if (is != null) com.example.hzg.mysussr.Utils.closed(is);
            if (err != null) com.example.hzg.mysussr.Utils.closed(err);
        }
        return new String[]{message.toString(), errMessage.toString()};

    }


  public   static void editTextFileWithShellandStream(final Context context, final String tempPath, final String path) {

        String result = ShellTool.execShell(new String[]{"cat " + path}, true, true)[0];
        Log.d("readTextFile", "result=" + result);
        if (result != null && !result.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(path + "(shell模式)");
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
                        ShellTool.execShell(new String[]{"cat " + tempPath + " > /data/sussr/setting.ini"}, true, true);
                    } catch (IOException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
            builder.setNegativeButton("放弃修改", null);
            builder.show();
        }

    }


    /**
     * @param context         上下文
     * @param cmd             shell命令数组
     * @param isRoot          使用以root权限运行
     * @param isGetErrMessage 是否捕获错误信息
     */
   public static void execShellTask(Context context, String[] cmd, boolean isRoot, boolean isGetErrMessage) {
        ShellTask task = new ShellTask(context, cmd, isRoot, isGetErrMessage);
        task.execute();

    }

    /**
     * shell命令执行ShellTask
     */
    private static class ShellTask extends AsyncTask<Void, Void, String[]> {
        private AlertDialog.Builder builder;
        private ProgressDialog dialog;
        private Context mContext;
        String[] mcmd;
        boolean isroot, isgeteer;
        private String logTag = "ShellTask";

        ShellTask(Context context, String[] cmd, boolean isRoot, boolean isGetErrMessage) {
            mContext = context;
            mcmd = cmd;
            isroot = isRoot;
            isgeteer = isGetErrMessage;
            Log.d(logTag, "create");
        }

        @Override
        protected String[] doInBackground(Void... params) {
            Log.d(logTag, "doInBackground");

            return execShell(mcmd, isroot, isgeteer);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(logTag, "onPreExecute");
            dialog = ProgressDialog.show(mContext, "脚本执行", "脚本执行中，请稍等........", true, !isgeteer);

        }


        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            Log.d(logTag, "onPostExecute");
            dialog.dismiss();
            builder = new AlertDialog.Builder(mContext);
            builder.setTitle("执行结果");
            builder.setMessage("输出信息：\n" + strings[0] + "\n" + "错误信息：\n" + strings[1] + "\n");
            builder.setNegativeButton("确定", null);

            builder.create().show();
            mContext = null;

        }
    }
}
