package com.example.hzg.mysussr;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
        OutputStream os=null;
        InputStream is=null;
        InputStream err=null;
        try {
            if (isroot)
                p = Runtime.getRuntime().exec("su");
            else {
                p = Runtime.getRuntime().exec(cmd[0]);
            }

           os=p.getOutputStream();
           is=p.getInputStream();
           err=p.getErrorStream();
            for (String line:cmd)
            {
                Log.d("ShellTool#cmd",line);
                os.write((line+"\n").getBytes());
            }
            os.flush();
            os.close();
            os=null;
            BufferedReader read=new BufferedReader(new InputStreamReader(is));
            String line=null;
            while((line=read.readLine())!=null)
            {
                Log.d("ShellTool#outputText",line);
                message.append(line+"\n");
            }
            is.close();
            is=null;
            read.close();
            if (isgeterrmessage) {
                BufferedReader read_err = new BufferedReader(new InputStreamReader(err));
                while ((line = read_err.readLine()) != null) {
                    Log.d("ShellTool#errText",line);
                    errMessage.append(line + "\n");
                }
                err.close();
                err=null;
               read_err.close();
            }
            else {
                err.close();
                err=null;
            }


        } catch (IOException e) {
            e.printStackTrace();
            errMessage.append(e.getMessage());
        }finally {
            if (p!=null)
            p.destroy();
            if (os!=null)Utils.closed(os);
            if (is!=null)Utils.closed(is);
            if (err!=null)Utils.closed(err);
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

    /**
     * 客户端执行shell命令使用方法
     * @deprecated  逻辑不清晰，使用复杂
     * @param cmd          待执行命令
     * @param isroot       是否以root权限执行
     * @param isgeterrmessage  是否获取错误信息
     * @param handler        调用者的handler
     */
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

    /**
     * 客户端执行shell命令使用方法
     * @param context
     * @param cmd
     * @param isRoot
     * @param isGetErrMessage
     */
    public  static void  execShellTask(Context context,String[] cmd,boolean isRoot,boolean isGetErrMessage)
    {
        ShellTask task=new ShellTask(context,cmd,isRoot,isGetErrMessage);
        task.execute();

    }

    /**
     * shell命令执行ShellTask
     */
    static class  ShellTask extends AsyncTask<Void,Void,String[]>
    {
       private AlertDialog.Builder builder;
      private ProgressDialog dialog;
        private  Context mContext;
public static  void  relase()
{


}
        String[] mcmd;
        boolean isroot,isgeteer;
        private  String  logTag="ShellTask";
        public ShellTask(Context context,String[] cmd,boolean isRoot,boolean isGetErrMessage) {
            mContext=context;
            mcmd=cmd;
            isroot=isRoot;
            isgeteer=isGetErrMessage;
            Log.d(logTag,"create");
        }

        @Override
        protected String[] doInBackground(Void... params) {
            Log.d(logTag,"doInBackground");

            String[] result=execShell(mcmd,isroot,isgeteer);
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(logTag,"onPreExecute");
            dialog=ProgressDialog.show(mContext,"脚本执行","脚本执行中，请稍等........",true,!isgeteer);

        }



        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            Log.d(logTag,"onPostExecute");
            dialog.dismiss();
            builder=new AlertDialog.Builder(mContext);
            builder.setTitle("执行结果");
            builder.setMessage("输出信息：\n"+strings[0]+"\n"+"错误信息：\n"+strings[1]+"\n");
            builder.setNegativeButton("确定",null);

            builder.create().show();
            mContext=null;

        }
    }
}
