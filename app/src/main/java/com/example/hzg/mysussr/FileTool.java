package com.example.hzg.mysussr;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by hzg on 2017/2/7.
 */

public class FileTool {
    public static boolean copyFileFromAssets(Context context,String path) {
        File dir=new File(path);
        if (!dir.exists())
            dir.mkdir();
        try {
            String[] paths= context.getAssets().list("");
            for (String line:paths) {
                System.out.println(line);
                if (context.getAssets().list(line).length==0)
                    FileTool.copyFile(context,line,path+"/"+line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        }
        return  true;
    }
    //从assets文夹  获取文件  复制到目标
    public static boolean copyFile(Context context, String path, String newpath)
    {
        try {
            InputStream is=context.getAssets().open(path);
//            File file=new File(newpath);
//            if (!file.exists())file.createNewFile();
            OutputStream os=new  FileOutputStream(newpath);
            byte[] bytes=new byte[1024];
            int len=0;
            while((len=is.read(bytes))!=-1)
            {
                os.write(bytes,0,len);
            }
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        }
        return true;
    }
    public static  void editTextFileWithStream(final Context context, final String path) {
    String result=null;
        result= readTextFile(context, path);
    Log.d("readTextFile","result="+result);
    if (result!=null&&result!="") {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(path);
        final EditText editText1 = new EditText(context);
        editText1.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText1.setText(result);
        builder.setView(editText1);
        builder.setPositiveButton("确定修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    OutputStream os = new FileOutputStream(path);
                    os.write((editText1.getText().toString()).getBytes());
                    os.close();
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


    public static String readTextFile(Context context, String path) {
        StringBuilder sbuilder=new StringBuilder();
        try {
            InputStream is= new FileInputStream(path);
            BufferedReader reader=new BufferedReader(new InputStreamReader(is));
            String line;

            while((line=reader.readLine())!=null)
            {
                sbuilder.append(line+"\n");
            }
            is.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();

        }
        return  sbuilder.toString();
    }
}
