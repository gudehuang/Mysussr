package com.example.hzg.mysussr;

import android.content.Context;
import android.util.Base64;

import com.example.hzg.mysussr.bean.ConfigBean;
import com.example.hzg.mysussr.utils.ArrayUtils;
import com.example.hzg.mysussr.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by hzg on 2017/3/12.
 */

public class ConfigToolnew {

    String[] cofigValues;
    String[] header;
    String[] mConfigName;
    private String separator = "\\\n";
    private String formatString = null;
    private ArrayList<String[]> datalist;
    private ArrayList<ConfigBean> mConfigList;
    private int position = 0;
    public static final String SUSSR_DIR = "/data/sussr/";
    public static String StartSussrShell = SUSSR_DIR + "start.sh";
    public static String StopSussrShell = SUSSR_DIR + "stop.sh";
    public static String CheckSussrShell = SUSSR_DIR + "check.sh";
    public static String[] Remove_SUSSR = new String[]{"busybox rm -R " + SUSSR_DIR};
    public static String[] Inatall_SUSSR = new String[]{"mkdir " + SUSSR_DIR, "unzip -o " + StartAct.sussrInstallPath + " -d " + SUSSR_DIR,
            "chmod -R 777 " + SUSSR_DIR};
    public static String[] ReInstall_Sussr = ArrayUtils.concat(Remove_SUSSR, Inatall_SUSSR);
    String[] StopSussr = new String[]{StopSussrShell};
    String[] CheckSussr = new String[]{CheckSussrShell};

    private void initFormatString() {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mConfigName.length; i++) {
            builder.append(mConfigName[i] + "=%s");
            if (i < mConfigName.length - 1) {
                builder.append(separator);
            }
        }
        formatString = builder.toString();
    }

    public ConfigToolnew(ArrayList<String[]> datalist, int position) {
        this.datalist = datalist;
        this.position = position;
    }

    public ConfigToolnew(Context context, String dataPath, int position) {

        File file = new File(dataPath);
        cofigValues = context.getResources().getStringArray(R.array.configValues);
        mConfigName = context.getResources().getStringArray(R.array.configName);
        header = context.getResources().getStringArray(R.array.configTitle);
        initFormatString();
        if (file.exists()) {
            boolean isreadSuccess = false;
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                datalist = (ArrayList<String[]>) objectInputStream.readObject();
                if (datalist.get(0).length < header.length - 1) {
                    int oldlength = datalist.get(0).length;
                    int newlength = header.length - 1;
                    for (int i = 0; i < datalist.size(); i++) {
                        String[] temp = new String[newlength];
                        String[] src = datalist.get(i);
                        for (int a = 0; a < newlength; a++) {
                            if (a < oldlength)
                                temp[a] = src[a];
                            else temp[a] = "0";
                        }

                        datalist.set(i, temp);

                    }
                }
                isreadSuccess = true;
            } catch (IOException e) {
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();

            } finally {
                if (!isreadSuccess) {
                    initDataList();
                }
            }
        } else {
            initDataList();
        }
        this.position = position;

    }

    public void initDataList() {
        datalist = new ArrayList<>();
        String[] defaultSetting = getDefaultConfigItem("default");
        datalist.add(defaultSetting);
    }

    public String[] getDefaultConfigItem(String itemname) {
        String[] item = cofigValues.clone();
        item[0] = itemname;
        return item;
    }


    public String getParamString(Context context, int position) {
        String[] data = datalist.get(position);
        String TFX = Utils.getStringFromSharePre(context, "TCPFX");
        String UFX = Utils.getStringFromSharePre(context, "UDPFX");
        String UJW = Utils.getStringFromSharePre(context, "UDPJW");
        return String.format(formatString,
                data[1], data[2], data[3], data[4], data[5]
                , data[6], data[7], data[8], data[9], data[10]
                , data[11], data[12], data[13], data[14], data[15]
                , data[16], data[17], data[18], TFX, UFX, UJW);
    }

    public String[] getHeader() {
        return header;
    }

    public void saveConfig(String savePath) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(savePath));
            objectOutputStream.writeObject(datalist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getStartShell(Context context, int position) {

        return new String[]{"sed -i '2," +
                (mConfigName.length + 1) + "d' /data/sussr/setting.ini",
                "sed -i \'1a " + getParamString(context, position) + "\' /data/sussr/setting.ini", StartSussrShell};
    }

    public String[] getStopShell() {
        return StopSussr;
    }

    public String[] getCheckShell() {
        return CheckSussr;
    }

    public String[] getRemoveShell() {
        return Remove_SUSSR;
    }

    public String[] getInstallShell() {

        return ReInstall_Sussr;
    }

    public ArrayList<String[]> getDatalist() {
        if (datalist == null) {
            initDataList();
        }
        return datalist;
    }

    public String share(String[] config) {
        StringBuilder builder = new StringBuilder();
        builder.append("sussr://");
        int i = 0;
        for (String s : config) {
            i++;
            builder.append(s);
            if (i < config.length) {
                builder.append(":");
            }

        }
        builder.append("/");
        System.out.println(builder.toString());
        return new String(Base64.encode(builder.toString().getBytes(), Base64.DEFAULT));
    }

    /***
     * 解析ssr链接
     * @param ssr
     * @return
     */
    public String[] getConfigItemFromSSR(String ssr) {
        System.out.println(ssr);
        String[] result = ssr.split("/");
        String string = result[2];
        System.out.println(string);
        String s = string.replace('_', '/');
        System.out.println(s);
        String[] item = null;
        try {
            byte[] bytes = Base64.decode(s, Base64.DEFAULT);
            String decodeString = new String(bytes);
            /**
             * 解析的数据parms有6个
             * parms[0] ip
             * parms[1] port
             * parms[2] 协议
             * prams[3] 加密方法
             * parms[4] 混淆方式
             * parms[5] 密码
             */
            String[] parms = decodeString.split("/")[0].split(":");
            parms[5] = new String(Base64.decode(parms[5], Base64.DEFAULT));
            System.out.println(decodeString);
            item = new String[]{parms[0], parms[0], parms[1], parms[5], cofigValues[4],
                    parms[3], parms[2], parms[4], cofigValues[8], cofigValues[9],
                    cofigValues[10], cofigValues[11], cofigValues[12], cofigValues[13], cofigValues[14],
                    cofigValues[15], cofigValues[16], cofigValues[17], cofigValues[18]};
        } catch (Exception e) {
               return  null;
        }
        return item;
    }

    public String[] getConfigItemFromSUSSR(String sussr) {
        System.out.println(sussr);
        String[] result = sussr.split("/");
        String string = result[2];
        String[] item = null;
        try {
            byte[] bytes = Base64.decode(string, Base64.DEFAULT);
            String decodeString = new String(bytes);
            String[] parms = decodeString.split("/")[0].split(":");
            System.out.println(decodeString);
            item = parms;
            for (String i : parms) {
                System.out.println(i);
            }
        } catch (Exception e) {

        }
        return item;
    }

    public void release() {
        datalist = null;
    }
}
