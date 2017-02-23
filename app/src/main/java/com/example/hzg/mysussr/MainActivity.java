package com.example.hzg.mysussr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView applistview;
    Button btnstart, btnstop, btncheck;
    FloatingActionButton btnIp;
    public static final String SUSSR_DIR = "/data/sussr/";
    public static String StartSussrShell= SUSSR_DIR + "start.sh";
    public static String StopSussrShell = SUSSR_DIR + "stop.sh";
    public static String CheckSussrShell = SUSSR_DIR + "check.sh";
    private String dataPath = StartAct.sussrPath+"/datalist";
    public String[] Inatall_SUSSR = new String[]{"mkdir " + SUSSR_DIR, "unzip -o " + StartAct.sussrInstallPath + " -d " + SUSSR_DIR,
            "chmod -R 777 " + SUSSR_DIR};
    String[] StopSussr = new String[]{StopSussrShell};
    String[] CheckSussr = new String[]{CheckSussrShell};
    private String[] Remove_SUSSR = new String[]{"busybox rm -R " + SUSSR_DIR};
    private  ProgressDialog dialog;
    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case ShellTool.EXEC_SHELL_HANDLER:

                  if (dialog!=null) {
                      dialog.dismiss();
                      dialog = null;
                  }
                   String[] result= (String[]) msg.obj;
                   AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                   builder.setTitle("执行结果");
                   builder.setMessage("输出信息：\n"+result[0]+"\n"+"错误信息：\n"+result[1]+"\n");
                   builder.setNegativeButton("确定",null);
                   builder.create().show();
                   break;


           }
        }
    };
    MyAdapter adapter;
   //储存配置项的容器，配置项为String[] 数组
    ArrayList<String[]> datalist;
    //记录当前配置项在容器的序号
    private  int position;
    //记录是否开机启动脚本的变量
    private  boolean isbootstart=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        applistview = (RecyclerView) findViewById(R.id.applistview);
        btncheck = (Button) findViewById(R.id.check_btn);
        btnstart = (Button) findViewById(R.id.start_btn);
        btnstop = (Button) findViewById(R.id.stop_btn);
        btnIp= (FloatingActionButton) findViewById(R.id.checkip_btn);
        btnstart.setOnClickListener(this);
        btnstop.setOnClickListener(this);
        btncheck.setOnClickListener(this);
        btnIp.setOnClickListener(this);
        //
        initParams();
        applistview.setLayoutManager(new LinearLayoutManager(this));
        if (position>=datalist.size())position=0;
        adapter=new MyAdapter(datalist,position);
        applistview.setAdapter(adapter);
    }

    private void initParams() {
        SharedPreferences preferences=getSharedPreferences("sussr",MODE_PRIVATE);
        position=preferences.getInt("position",0);
        isbootstart=preferences.getBoolean("boot",false);
        //从文件中获取容器
        readDataList();
    }

    private void readDataList() {
        File file=new File(dataPath);
        if (file.exists()) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                datalist = (ArrayList<String[]>) objectInputStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();

            }
        }
        else {
            initDataList();}
    }

    private void initDataList() {
        datalist = new ArrayList<>();
        String[] defaultSetting = getDefaultItem("default");
        datalist.add(defaultSetting);
    }

    @NonNull
    private String[] getDefaultItem(String itemname) {
        return new String[]{itemname," ","80"," ","supppig",
                    "chacha20","auth_sha1","http_simple","114.255.201.163","114.114.114.114",
                    "1","1","1","0","0"};
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(dataPath));
            objectOutputStream.writeObject(datalist);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences preferences=getSharedPreferences("sussr",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("position",position);
        editor.putBoolean("boot",isbootstart);
        editor.commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,100,0,"安装sussr.zip");
        menu.add(0,200,0,"安装busybox");
        menu.add(0,300,0,"卸载sussr.zip");
        menu.add(0,400,0,"重置应用");
        menu.add(0,500,0,"选择配置").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0,600,0,"新增配置").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0,700,0,"修改文件").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case  100:
                dialog=ProgressDialog.show(this,"脚本执行","脚本执行中，请稍等........",true,false);
                ShellTool.execShellWithHandler(Inatall_SUSSR,true,true,handler);
                break;
            case  200:
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://"+StartAct.BusyboxInstallPath),"application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case  300:
                dialog=ProgressDialog.show(this,"脚本执行","脚本执行中，请稍等........",true,false);
                ShellTool.execShellWithHandler(Remove_SUSSR,true,true,handler);


                break;
            case  400:
                SharedPreferences.Editor editor=getSharedPreferences("sussr",MODE_PRIVATE).edit();
                editor.putBoolean("isaccept",false);
                editor.commit();
                Intent intent1=new Intent(this,StartAct.class);
                startActivity(intent1);
                finish();
                break;
            case  500:
                AlertDialog.Builder builder= new AlertDialog.Builder(this);
                builder.setTitle("选择配置");
                String[] items=new String[datalist.size()];
                for (int i=0;i<items.length;i++)
                {
                    items[i]=datalist.get(i)[0];
                }
                final int[] select = {position};
                builder.setSingleChoiceItems(items, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select[0] =which;
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (position!=select[0])position=select[0];
                        adapter.setDatePosition(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (datalist.size()>1)
                        {datalist.remove(select[0]);
                            if (position==select[0]) {
                                position=0;
                                adapter.setDatePosition(position);
                                adapter.notifyDataSetChanged();
                            }
                            else  if (position>select[0]) {
                                position--;
                            }
                        }
                    }
                });
                builder.show();
                break;
            case 600:
                AlertDialog.Builder builder1=new AlertDialog.Builder(this);
                builder1.setTitle("配置名称");
                final EditText inputText = new EditText(this);
                inputText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                builder1.setPositiveButton("新建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] defaultSetting=getDefaultItem(inputText.getText().toString());
                        datalist.add(defaultSetting);
                        position=datalist.size()-1;
                        adapter.setDatePosition(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder1.setNegativeButton("取消",null);
                builder1.setView(inputText);
                builder1.show();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        inputText.setFocusable(true);
                        inputText.setFocusableInTouchMode(true);
                        //请求获得焦点
                        inputText.requestFocus();
                        //调用系统输入法
                        InputMethodManager inputManager = (InputMethodManager) inputText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.showSoftInput(inputText, 0);
                    }
                }, 200);
                break;
            case 700:
                if (Build.VERSION.SDK_INT>19) {
                    ShellTool.editTextFileWithShellandStream(this,StartAct.sussrPath+"/temp" ,"/data/sussr/setting.ini");
                }
                else  FileTool.editTextFileWithStream(this,"/data/sussr/setting.ini");

                break;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.check_btn:
                dialog=ProgressDialog.show(this,"脚本执行","脚本执行中，请稍等........",true,false);
                        ShellTool.execShellWithHandler(CheckSussr,true,true,handler);
                break;
            case  R.id.start_btn:
                dialog=ProgressDialog.show(this,"脚本执行","脚本执行中，请稍等........",true,true);
                String str = getParamString();
                ShellTool.execShellWithHandler(new String[]{"sed -i '2,15d' /data/sussr/setting.ini",
                                "sed -i '1a "+str  + "' /data/sussr/setting.ini",StartSussrShell},true,true,handler);
                break;
            case  R.id.stop_btn:
                dialog=ProgressDialog.show(this,"脚本执行","脚本执行中，请稍等........",true,false);
                ShellTool.execShellWithHandler(StopSussr,true,true,handler);
                break;
            case  R.id.checkip_btn:
                final WebView webview=new WebView(this);
                webview.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                webview.loadUrl("http://m.ip138.com/");
                webview.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                       webview.loadUrl(url);
                        return  true;
                    }});
                AlertDialog.Builder webviewBuilder=new AlertDialog.Builder(this);
                webviewBuilder.setView(webview);
                webviewBuilder.setNegativeButton("关闭",null);
                webviewBuilder.show();

        }
    }

    public String getParamString() {
        String[] data=datalist.get(position);
        String s="IP=%s\\\nPORT=%s\\\nPASSWORD=\"%s\"\\\nGOSTPWD=\"%s\"\\\nMETHOD=%s\\\nPROTOCOL=%s\\\nOBFS=%s"+
                "\\\nHOST=\"%s\"\\\nDNS=%s\\\nDLUDP=%s\\\nBJUDP=%s\\\nGXUDP=%s\\\nQJDL=%s\\\nPBQ=%s";
        System.out.println(s);
        return String.format(s,
                data[1],data[2],data[3],data[4],data[5]
                ,data[6],data[7],data[8],data[9],data[10]
                ,data[11],data[12],data[13],data[14]);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;
        EditText contentText;
        Switch switchText;
        public MyViewHolder(View itemView) {
            super(itemView);
            headerText = (TextView) itemView.findViewById(R.id.header_text);
            contentText = (EditText) itemView.findViewById(R.id.content_text);
            switchText= (Switch) itemView.findViewById(R.id.switch_text);

        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private  ArrayList<String[]> datalist;
        private String[] data ;
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
                3
        };
        private String[] header =
                {"配置名称", "服务器", "端口", "密码","gost密码（udp转发为2时有效）",
                "加密方法", "协议", "混淆方式", "混淆参数", "DNS地址",
                "UDP转发(0直连/1服务器转发UDP/2TCP转发)","本机UDP放行（禁网/放行）","热点UDP放行（禁网/放行）","连接WIFI时强制使用ssr代理","破视频版权",
                "开机自启脚本"
        };

        public MyAdapter(ArrayList<String[]> datalist,int position) {
            this.datalist = datalist;
            setDatePosition(position);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            MyViewHolder holder = new MyViewHolder(root);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            Log.d("onBindViewHolder","start;position:"+position);
            holder.headerText.setText(header[position]);
            switch (type[position])
            {
                case 0:
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.switchText.setVisibility(View.GONE);
                    holder.contentText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    holder.contentText.setText(data[position]);
                    holder.contentText.setOnClickListener(getListener(holder,position,0));
                    break;
                case 1:
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.switchText.setVisibility(View.GONE);
                    holder.contentText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    holder.contentText.setText(data[position]);
                    holder.contentText.setOnClickListener(getListener(holder,position,1));
                    break;
                case  2:
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.switchText.setVisibility(View.GONE);
                    holder.contentText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    holder.contentText.setText(data[position]);
                    holder.contentText.setOnClickListener(getListener(holder,position,2));
                    break;
                case  3:
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.switchText.setVisibility(View.VISIBLE);
                    holder.contentText.setText("");
                    holder.contentText.setOnClickListener(getListener(holder,position,3));
                    //recycleview 对switch复用会导致switch里的监听器
                    holder.switchText.setOnCheckedChangeListener(null);
                    //开机自启开关位于列表最后一项
                    if (position==header.length-1) {
                        Log.d("BootStart",""+isbootstart+"position:"+ position);
                        holder.switchText.setChecked(isbootstart);
                    }
                    else {
                        if (data[position].equals("1"))
                            holder.switchText.setChecked(true);
                        else holder.switchText.setChecked(false);
                    }
                    holder.switchText.setOnCheckedChangeListener(getSwitchListener(holder, position,3));
                     break;
                case  4:
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.switchText.setVisibility(View.VISIBLE);
                    holder.contentText.setText(data[position]);
                    holder.contentText.setOnClickListener(getListener(holder,position,4));
                    //recycleview 对switch复用会导致switch里的监听器
                    holder.switchText.setOnCheckedChangeListener(null);
                    //开机自启开关位于列表最后一项
                    if (position==header.length-1) {
                        Log.d("BootStart",""+isbootstart+"position:"+ position);
                        holder.switchText.setChecked(isbootstart);
                    }
                    else {
                        if (data[position].equals("1"))
                            holder.switchText.setChecked(true);
                        else holder.switchText.setChecked(false);
                    }
                    holder.switchText.setOnCheckedChangeListener(getSwitchListener(holder, position,4));
                    break;
            }


        }

        @NonNull
        public CompoundButton.OnCheckedChangeListener getSwitchListener(final MyViewHolder holder, final int position, final int type) {
            return new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (position==header.length-1)
                    {
                        Log.d("switchOnChecked","isboot:"+isbootstart);
                        isbootstart=isChecked;
                        Log.d("switchOnChecked",position+":"+isChecked+":"+isbootstart);
                    }
                    else {
                        if (isChecked) data[position] = "1";
                        else data[position] = "0";
                    }
                    if (type==4)holder.contentText.setText(data[position]);

                }
            };
        }

        @Nullable
        public View.OnClickListener getListener(final MyViewHolder holder, final int position, final int type) {
            View.OnClickListener listener=null;
            if(type!=3) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击弹出dialog窗口
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle(header[position]);
                        View root=null;
                        //列表5-7 窗口内容为spinner
                        if (type==2) {
                            Spinner spinner=new Spinner(v.getContext());
                            spinner.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            spinner.setPadding(16,0,16,0);
                            final String[][] items = new String[][]{{""}};
                            switch (position) {
                                case 5:
                                    items[0] = getResources().getStringArray(R.array.item1);
                                    break;
                                case 6:
                                    items[0] = getResources().getStringArray(R.array.item2);
                                    break;
                                case 7:
                                    items[0] = getResources().getStringArray(R.array.item3);
                                    break;
                            }
                            ArrayList<String> item = new ArrayList<String>(Arrays.asList(items[0]));
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, items[0]);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(spinnerAdapter);
                            spinner.setSelection(item.indexOf(data[position]));
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                                    data[position] = items[0][position1];
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            root=spinner;

                        } else {
                            //其他列表项 窗口内容为EditText
                            EditText editText=new EditText(v.getContext());
                            editText.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            editText.setText(data[position]);
                            editText.setSelection(data[position].length());
                            root=editText;
                        }

                       builder.setNegativeButton("取消",null);
                        final View finalRoot = root;
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               if (type==2)
                               data[position] = ((EditText) finalRoot).getText().toString().trim();

                               holder.contentText.setText(data[position]);

                           }
                       }) ;
                        builder.setView(root);
                        builder.show();
                        if (type!=2) { //延时弹出软键盘
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    finalRoot.setFocusable(true);
                                    finalRoot.setFocusableInTouchMode(true);
                                    //请求获得焦点
                                    finalRoot.requestFocus();
                                    //调用系统输入法
                                    InputMethodManager inputManager = (InputMethodManager) finalRoot.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.showSoftInput(finalRoot, 0);
                                }
                            }, 200);
                        }
                    }
                };
            }
            return listener;
        }

        @Override
        public int getItemCount() {
            return header.length;
        }

        public void setDatePosition(int position) {
            data=datalist.get(position);
            if (data.length<header.length-1)
            {
                ArrayList<String> temp=new ArrayList<>(Arrays.asList(data));
                while (temp.size()<header.length-1)
                {
                    temp.add("0");
                }
                data= (String[]) temp.toArray();
            }
        }
    }

}
