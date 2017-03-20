package com.example.hzg.mysussr;

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

public class ConfigTool {
    private ArrayList<String[]> datalist;
    private  int position=0;
    public static final String SUSSR_DIR = "/data/sussr/";
    public static String StartSussrShell= SUSSR_DIR + "start.sh";
    public static String StopSussrShell = SUSSR_DIR + "stop.sh";
    public static String CheckSussrShell = SUSSR_DIR + "check.sh";
    public String[] Inatall_SUSSR = new String[]{"mkdir " + SUSSR_DIR, "unzip -o " + StartAct.sussrInstallPath + " -d " + SUSSR_DIR,
            "chmod -R 777 " + SUSSR_DIR};
    String[] StopSussr = new String[]{StopSussrShell};
    String[] CheckSussr = new String[]{CheckSussrShell};
    private String[] Remove_SUSSR = new String[]{"busybox rm -R " + SUSSR_DIR};
    public ConfigTool(ArrayList<String[]> datalist,int position) {
        this.datalist = datalist;
        this.position=position;
    }

    public ConfigTool(String dataPath,int position) {

        File file=new File(dataPath);
        if (file.exists()) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                datalist = (ArrayList<String[]>) objectInputStream.readObject();
                if (datalist.get(0).length<header.length-1)
                {
                    int  oldlength=datalist.get(0).length;
                    int  newlength=header.length-1;
                    for (int i=0;i<datalist.size();i++)
                    {
                        String[] temp=new String[newlength];
                        String[] src=datalist.get(i);
                        for (int a=0;a<newlength;a++)
                        {
                            if (a<oldlength)
                                temp[a]=src[a];
                            else  temp[a]="0";
                        }

                        datalist.set(i, temp);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                datalist = new ArrayList<>();
                String[] defaultSetting = getDefaultConfigItem("default");
                datalist.add(defaultSetting);
            }
        }
        else {
            datalist = new ArrayList<>();
            String[] defaultSetting = getDefaultConfigItem("default");
            datalist.add(defaultSetting);}
        this.position = position;
    }

    public String[] getDefaultConfigItem(String itemname) {
        return new String[]{itemname," ","80"," ","supppig",
                "chacha20","auth_sha1","http_simple","114.255.201.163","114.114.114.114",
                "1","1","1","0","0","0","0","0"};
    }
    private  String separator="\\\n";
    public String getParamString(int position) {
        String[] data=datalist.get(position);
        String s="IP=%s" +separator+
                "PORT=%s" +separator+
                "PASSWORD=%s" +separator+
                "GOSTPWD=%s" +separator+
                "METHOD=%s"  +separator+

                "PROTOCOL=%s" +separator+
                "OBFS=%s"  +separator+
                "HOST=%s" +separator+
                "DNS=%s"  +separator+
                "DLUDP=%s"  +separator+

                "BJUDP=%s"  +separator+
                "GXUDP=%s"  +separator+
                "QJDL=%s"  +separator+
                "PBQ=%s" +separator+
                "DATAADB=%s" +separator+

                "HTTPSGL=%s" +separator+
                "HOTADB=%s";
        System.out.println(s);
        return String.format(s,
                data[1],data[2],data[3],data[4],data[5]
                ,data[6],data[7],data[8],data[9],data[10]
                ,data[11],data[12],data[13],data[14],data[15]
                ,data[16],data[17]);
    }

    /*0 edittext             隐藏开关按钮          弹出窗口为编辑框
    /*1 edittext（隐藏密码） 隐藏开关按钮          弹出窗口为编辑框
    /*2 edittext             隐藏开关按钮          弹出窗口为选项表
    /*3 edittext(无文字)     开关按钮              无弹出窗口
    /*4 edittext(有文字)     开关按钮              弹出窗口为编辑框
    * */
    private  int[] type={
            0,0,0,1,1,
            2,2,2,0,0,
            4,3,3,3,3,
            3,3,3,3
    };
    private String[] header =
            {"配置名称", "服务器", "端口", "密码","gost密码（udp转发为2时有效）",
                    "加密方法", "协议", "混淆方式", "混淆参数", "DNS地址",
                    "UDP转发(0直连/1服务器转发UDP/2TCP转发)","本机UDP放行（禁网/放行）","热点UDP放行（禁网/放行）","连接WIFI时强制使用ssr代理","破视频版权",
                    "广告过滤","HTTPS过滤","热点过滤",
                    "开机自启脚本"
            };

    public int[] getType() {
        return type;
    }

    public String[] getHeader() {
        return header;
    }
    public  void  saveConfig(String savePath)
    {
        try {
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(savePath));
            objectOutputStream.writeObject(datalist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String[] getStartShell(int position) {

       return new String[]{"sed -i '2," +
               (header.length-1)+"d' /data/sussr/setting.ini",
                "sed -i \'1a " + getParamString(position) + "\' /data/sussr/setting.ini", StartSussrShell};
    }
    public String[] getStopShell()
    {
        return  StopSussr;
    }

    public  String[] getCheckShell()
    {
        return  CheckSussr;
    }
    public  String[] getRemoveShell()
    {
        return  Remove_SUSSR;
    }
    public  String[] getInstallShell()
    {
        return  Inatall_SUSSR;
    }

    public ArrayList<String[]> getDatalist() {
        return datalist;
    }
}
