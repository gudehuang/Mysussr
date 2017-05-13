package com.example.hzg.mysussr.utils;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by hzg on 2017/2/7.
 * 文件读写类，用于文件复制
 */

public class FileTool {
  public   static boolean copyFileFromAssets(Context context, String dstPath) {
        File dir = new File(dstPath);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                return false;
            }
        }

      try {
          String[] paths = context.getAssets().list("");
          for (String line : paths) {
              System.out.println(line);
              if (context.getAssets().list(line).length == 0) {
                  if (line.contains("apk") || line.contains("zip")) {
                      Log.d("", "copy:" + line);
                      FileTool.copyFile(context, line, dstPath + "/" + line);
                  }
              }
          }
      } catch (IOException e) {
          e.printStackTrace();
          return false;
      }
        return true;
    }

    //从assets文夹  获取文件  复制到目标
    private static boolean copyFile(Context context, String path, String newpath) {
        File file=new File(newpath);
        if (file.exists())file.delete();
        try {
            InputStream is = context.getAssets().open(path);
            OutputStream os = new FileOutputStream(newpath);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void  editTextFileWithStream(final Context context, final String path) {

        String result = readTextFile(context, path);
        Log.d("readTextFile", "result=" + result);
        if (!result.equals("")) {
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
                    } catch (IOException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("放弃修改", null);
            builder.show();
        }

    }


    private static String readTextFile(Context context, String path) {
        StringBuilder sbuilder = new StringBuilder();
        try {
            InputStream is = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null) {
                sbuilder.append(line);
                sbuilder.append("\n");
            }
            is.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return sbuilder.toString();
    }

}
