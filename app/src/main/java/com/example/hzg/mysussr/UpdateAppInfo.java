package com.example.hzg.mysussr;

/**
 * Created by hzg on 2017/3/19.
 */

public class UpdateAppInfo {
    public UpdateInfo data;
    private int errorCode;
    private String errorMessage;
    public class  UpdateInfo{

        public  String apkName;
        public  String apkVersion;
        public  String apkUrl;
        public  String apkInfo;
        public  String apkDate;
        public  int apkVersionCode;

        @Override
        public String toString() {
            StringBuilder builder=new StringBuilder();
            builder.append("应用名称："+apkName+"\n");
            builder.append("应用版本："+apkVersion+"\n");
            builder.append("应用版本号："+apkVersionCode+"\n");
            builder.append("更新日期："+apkDate+"\n");
            builder.append("更新信息："+apkInfo+"\n");
            return builder.toString();
        }
    }
}
