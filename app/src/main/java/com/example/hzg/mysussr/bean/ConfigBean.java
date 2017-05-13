package com.example.hzg.mysussr.bean;

import android.app.SearchManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hzg on 2017/5/9.
 */

public class ConfigBean {
    private  String  configName;
    private  String  PORT;
    private  String  IP;
    private  String  PASSWORD;
    private  String  GOSTPWD;

    private  String  METHOD;
    private  String  PROTOCOL;
    private  String  OBFS;
    private  String  HOST;
    private  String  DNS;

    private  String  DLUDP;
    private  String  BJUDP;
    private  String  GXUDP;
    private  String  QJDL;
    private  String  PBQ;

    private  String  DATAADB;
    private  String  HOTADB;
    private  String  WIFIADB;
    private  String  AUTOUPDATE;

    private  int length=19;

    public ConfigBean() {
    }

    public ConfigBean(String[] src) {
        if (src.length>=length)
        {
            configName=src[0];
            IP=src[1];
            PORT=src[2];
            PASSWORD=src[3];
            GOSTPWD=src[4];
            METHOD=src[5];
            PROTOCOL=src[6];
            OBFS=src[7];
            HOST=src[8];
            DNS=src[9];
            DLUDP=src[10];
            BJUDP=src[11];
            GXUDP=src[12];
            QJDL=src[13];
            PBQ=src[14];
            DATAADB=src[15];
            HOTADB=src[16];
            WIFIADB=src[17];
            AUTOUPDATE=src[18];
        }
    }

     public  JSONObject converToJsonObject()
     {
         JSONObject object=new JSONObject();
         try {
             object.put("configName",configName);
             object.put("IP",IP);
             object.put("PORT",PORT);
             object.put("PASSWORD",PASSWORD);
             object.put("GOSTPWD",GOSTPWD);

             object.put("METHOD",METHOD);
             object.put("PROTOCOL",PROTOCOL);
             object.put("OBFS",OBFS);
             object.put("HOST",HOST);
             object.put("DNS",DNS);

             object.put("DLUDP",DLUDP);
             object.put("BJUDP",BJUDP);
             object.put("GXUDP",GXUDP);
             object.put("QJDL",QJDL);
             object.put("PBQ",PBQ);

             object.put("DATAADB",DATAADB);
             object.put("HOTADB",HOTADB);
             object.put("WIFIADB",WIFIADB);
             object.put("AUTOUPDATE",AUTOUPDATE);
         } catch (JSONException e) {
             e.printStackTrace();
         }
         return object;
     }
    @Override
    public String toString() {
        String result=null;
        JSONObject jsonObject=new JSONObject();
        JSONArray  jsonArray=new JSONArray();
        JSONObject object=new JSONObject();
        try {
            object.put("configName",configName);
            object.put("IP",IP);
            object.put("PORT",PORT);
            object.put("PASSWORD",PASSWORD);
            object.put("GOSTPWD",GOSTPWD);
            object.put("METHOD",METHOD);
            object.put("PROTOCOL",PROTOCOL);
            object.put("OBFS",OBFS);
            object.put("HOST",HOST);
            object.put("DNS",DNS);
            object.put("DLUDP",DLUDP);
            object.put("BJUDP",BJUDP);
            object.put("GXUDP",GXUDP);
            object.put("QJDL",QJDL);
            object.put("PBQ",PBQ);
            object.put("DATAADB",DATAADB);
            object.put("HOTADB",HOTADB);
            object.put("WIFIADB",WIFIADB);
            object.put("AUTOUPDATE",AUTOUPDATE);
            jsonArray.put(object);
            jsonObject.put("config",jsonArray);
            result=jsonObject.toString();
            Log.d(this.getClass().getSimpleName(),result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
