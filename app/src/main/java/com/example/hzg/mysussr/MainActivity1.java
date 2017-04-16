package com.example.hzg.mysussr;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 使用新布局 侧滑菜单  显示内容分模块
 */
public class MainActivity1 extends AppCompatActivity implements View.OnClickListener {
    RecyclerView applistview;
    Button btnstart, btnstop, btncheck;
    FloatingActionButton btnIp;
    private String dataPath = StartAct.sussrPath+"/datalist";
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
                   AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity1.this);
                   builder.setTitle("执行结果");
                   builder.setMessage("输出信息：\n"+result[0]+"\n"+"错误信息：\n"+result[1]+"\n");
                   builder.setNegativeButton("确定",null);
                   builder.create().show();
                   break;


           }
        }
    };
   ReAdapter adapter;
   //储存配置项的容器，配置项为String[] 数组
    ArrayList<String[]> datalist;
    //记录当前配置项在容器的序号
    private  int position;
    //记录是否开机启动脚本的变量
    private  boolean isbootstart=false;
    private  ConfigTool mConfigTool;
    private  DownloadBroadcastReceiver downloadBroadcastReceiver;
    private NavigationView navigationView;
    private  MenuTool mMenuTool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        setContentView(R.layout.main);

        //初始化控件
        applistview = (RecyclerView) findViewById(R.id.applistview);
        btncheck = (Button) findViewById(R.id.check_btn);
        btnstart = (Button) findViewById(R.id.start_btn);
        btnstop = (Button) findViewById(R.id.stop_btn);
        btnIp= (FloatingActionButton) findViewById(R.id.checkip_btn);
        navigationView= (NavigationView) findViewById(R.id.main_navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_install_sussr:
                       mMenuTool.menuInstall();
                        break;
                    case R.id.menu_install_busybox:
                       mMenuTool.menuInstallBusybox();
                        break;
                    case R.id.menu_uninstall_sussr:
                        mMenuTool.menuUninstall();
                        break;
                    case R.id.menu_uid:
                        mMenuTool.menuUid();
                        break;
                    case R.id.menu_help:
                        mMenuTool.menuHlep();
                        break;
                    case R.id.menu_reset:
                        mMenuTool.menuReset();
                        break;
                    case  R.id.menu_update:
                        mMenuTool.menuUpdate();
                        break;

                }
                return true;
            }
        });
        btnstart.setOnClickListener(this);
        btnstop.setOnClickListener(this);
        btncheck.setOnClickListener(this);
        btnIp.setOnClickListener(this);
        //
        initParams();
        applistview.setLayoutManager(new LinearLayoutManager(this));
        if (position>=datalist.size())position=0;
       // adapter=new MyAdapter(datalist,position);
        adapter=new ReAdapter();
        applistview.setAdapter(adapter);

    }

    private void initParams() {
        SharedPreferences preferences=getSharedPreferences("sussr",MODE_PRIVATE);
        position=preferences.getInt("position",0);
        isbootstart=preferences.getBoolean("boot",false);
        if (Build.VERSION.SDK_INT>21&& ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        mConfigTool=new ConfigTool(dataPath,position);
        mMenuTool=new MenuTool(this,handler,dialog,mConfigTool);
        datalist=mConfigTool.getDatalist();
        downloadBroadcastReceiver=new DownloadBroadcastReceiver();
        registerReceiver(downloadBroadcastReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1)
        {
            if (grantResults[0]==PackageManager.PERMISSION_DENIED)
            {
                finish();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mConfigTool.saveConfig(dataPath);
        SharedPreferences preferences=getSharedPreferences("sussr",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("position",position);
        editor.putBoolean("boot",isbootstart);
        editor.commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadBroadcastReceiver);
        mMenuTool.release();
        mConfigTool=null;
        System.out.println("onDestory");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case  R.id.menu_install_sussr:
                mMenuTool.menuInstall();
                break;
            case  R.id.menu_install_busybox:
                mMenuTool.menuInstallBusybox();
                break;
            case  R.id.menu_uninstall_sussr:
                mMenuTool.menuUninstall();
                break;
            case  R.id.menu_reset:
                mMenuTool.menuReset();
                break;
            case  R.id.menu_select_list:
                menuSelect();
                break;
            case R.id.menu_edit_setting:
                mMenuTool.menuEditSetting();

                break;
            case  R.id.menu_help:
                mMenuTool.menuHlep();
                break;
            case  R.id.menu_update:
                mMenuTool.menuUpdate();
                break;
            case R.id.menu_uid:
                mMenuTool.menuUid();
                break;

        }
        return true;
    }



    public void menuSelect() {
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
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (datalist.size()>1)
                {datalist.remove(select[0]);
                    if (position==select[0]) {
                        position=0;

                        adapter.notifyDataSetChanged();
                    }
                    else  if (position>select[0]) {
                        position--;
                    }
                }
                menuSelect();
            }
        });
        builder.setNegativeButton("新建", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder1=new AlertDialog.Builder(MainActivity1.this);
                builder1.setTitle("配置名称");
                final EditText inputText = new EditText(MainActivity1.this);
                inputText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                builder1.setPositiveButton("新建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] defaultSetting=mConfigTool.getDefaultConfigItem(inputText.getText().toString());
                        datalist.add(defaultSetting);
                        position=datalist.size()-1;
                        adapter.notifyDataSetChanged();
                    }
                });
                builder1.setNeutralButton("导入",null);
                builder1.setNegativeButton("取消",null);
                builder1.setView(inputText);
                final AlertDialog dialogCreate=builder1.show();
                //点击按钮，错误窗口不消失
                dialogCreate.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ssr=inputText.getText().toString();
                        if (ssr.indexOf("ssr://")==0) {
                            String[] defaultSetting = mConfigTool.getConfigItemFromSSR(ssr);
                            if (defaultSetting == null) {
                                Toast.makeText(MainActivity1.this, "导入失败,请输入正确的ssr链接", Toast.LENGTH_LONG).show();
                            } else {
                                datalist.add(defaultSetting);
                                position = datalist.size() - 1;
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity1.this, "导入成功", Toast.LENGTH_LONG).show();
                                dialogCreate.dismiss();

                            }
                        }
                        else {
                            Toast.makeText(MainActivity1.this, "请输入正确的ssr链接", Toast.LENGTH_LONG).show();

                        }
                    }

                });

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
            }
        });
        builder.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.check_btn:

                dialog=ProgressDialog.show(this,"脚本执行","脚本执行中，请稍等........",true,false);
                ShellTool.execShellWithHandler(mConfigTool.getCheckShell(),true,false,handler);
                break;
            case  R.id.start_btn:
                dialog=ProgressDialog.show(this,"脚本执行","脚本执行中，请稍等........",true,true);
                ShellTool.execShellWithHandler(mConfigTool.getStartShell(position),true,false,handler);
                break;
            case  R.id.stop_btn:
                dialog=ProgressDialog.show(this,"脚本执行","脚本执行中，请稍等........",true,false);
                ShellTool.execShellWithHandler(mConfigTool.getStopShell(),true,false,handler);
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

    class  ReAdapter extends  RecyclerView.Adapter<ReAdapter.MyHolder>
    {


        private String[] titles=mConfigTool.getModels();
        private  int[] mIndex=mConfigTool.getModleIndex();
        private  int[] mIdndexSize=mConfigTool.getModelsize();
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("ReAdapter", "onCreateViewHolder");
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.recyleitem,parent,false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
                holder.title.setText(titles[position]);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity1.this));
            if (position<1)
                 holder.recyclerView.setAdapter(new SwitchAdapter(datalist,mIndex[position],1,mIdndexSize[position]));
            else  holder.recyclerView.setAdapter(new SwitchAdapter(datalist,mIndex[position],2,mIdndexSize[position]));


        }



        @Override
        public int getItemCount() {
            return titles.length;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
        class  MyHolder extends  RecyclerView.ViewHolder {
            RecyclerView recyclerView;
            TextView title;
            public MyHolder(View itemView) {
                super(itemView);
                recyclerView= (RecyclerView) itemView.findViewById(R.id.item_recycleview);
                title= (TextView) itemView.findViewById(R.id.item_title);
            }
        }
    }
    class EditAdapter extends  RecyclerView.Adapter
    {
        private  String[] data;
        private  ArrayList<String[]> datalist;
        private  int startPosition=0;
        private  String[] header=mConfigTool.getHeader();

        public EditAdapter(ArrayList<String[]> datalist, int startPosition) {
            this.datalist=datalist;
            data=datalist.get(position);
            this.startPosition=startPosition;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("SwitchAdapter", "onCreateViewHolder");
            View  view=LayoutInflater.from(parent.getContext()).inflate(R.layout.edittext,parent,false);
            return new EditHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final EditHolder eholder= (EditHolder) holder;
            eholder.textview.setText(header[startPosition+position]);
            eholder.editText.setText(data[(startPosition+position)%data.length]);
            eholder.editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle(header[startPosition+position]);
                    final EditText editText=new EditText(v.getContext());
                    editText.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    editText.setText(data[startPosition+position]);
                    editText.setSelection(data[startPosition+position].length());
                    builder.setView(editText);
                    builder.setNegativeButton("取消",null);

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                                data[startPosition+position] = (editText).getText().toString().trim();

                            eholder.editText.setText(data[startPosition+position]);

                        }
                    }) ;

                    builder.show();
                    { //延时弹出软键盘
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                editText.setFocusable(true);
                                editText.setFocusableInTouchMode(true);
                                //请求获得焦点
                                editText.requestFocus();
                                //调用系统输入法
                                InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.showSoftInput(editText, 0);
                            }
                        }, 200);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return 5;
        }
        class  EditHolder extends RecyclerView.ViewHolder{
            TextView editText;
            TextView textview;
            public EditHolder(View itemView) {
                super(itemView);
                editText= (TextView) itemView.findViewById(R.id.item_edit);
                textview= (TextView) itemView.findViewById(R.id.item_header);
             }
        }
    }
    class SwitchAdapter extends  RecyclerView.Adapter
    {
        private  String[] data;
        private  ArrayList<String[]> datalist;
        private  int startPosition=0;
        private  String[] header=mConfigTool.getHeader();
        private  int type=-1;
        private  int size=0;
        public SwitchAdapter(ArrayList<String[]> datalist, int startPosition,int type,int size) {
            this.datalist=datalist;
            data=datalist.get(position);
            this.startPosition=startPosition;
            this.type=type;
            this.size=size;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("SwitchAdapter", "onCreateViewHolder");
            RecyclerView.ViewHolder holder=null;
            View  view=null;
            switch (type)
            {case  1:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.edittext,parent,false);
                holder=new EditHolder(view);
                break;
                case  2:
                    view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_switch,parent,false);
                    holder=new SwitchHolder(view);
                    break;
            }

            return  holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (type==1)
            {
                final EditHolder eholder = (EditHolder) holder;
                if (position>2&&position<5)
                    eholder.editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                else  eholder.editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eholder.textview.setText(header[startPosition + position]);
                eholder.editText.setText(data[(startPosition + position) % data.length]);
                eholder.editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle(header[startPosition + position]);
                        View root=null;
                        if (position>4&&position<8) {
                            Spinner spinner = new Spinner(v.getContext());
                            spinner.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            spinner.setPadding(16, 0, 16, 0);
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
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity1.this, android.R.layout.simple_spinner_item, items[0]);
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
                        }
                        else {
                            final EditText editText = new EditText(v.getContext());
                            editText.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            editText.setText(data[startPosition + position]);
                            editText.setSelection(data[startPosition + position].length());
                            root=editText;
                        }
                        builder.setView(root);
                        builder.setNegativeButton("取消", null);
                        final View finalRoot = root;
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               if (position>4&&position<8){}

                                else data[startPosition + position] = ((EditText)finalRoot).getText().toString().trim();
                                eholder.editText.setText(data[startPosition + position]);

                            }
                        });

                        builder.show();
                        if (root instanceof  EditText)
                        { //延时弹出软键盘
                            final View finalRoot1 = root;
                            new Handler().postDelayed(new Runnable() {
                                EditText editText= (EditText) finalRoot1;
                                public void run() {
                                    editText.setFocusable(true);
                                    editText.setFocusableInTouchMode(true);
                                    //请求获得焦点
                                    editText.requestFocus();
                                    //调用系统输入法
                                    InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.showSoftInput(editText, 0);
                                }
                            }, 200);
                        }
                    }
                });
            }
            else
        {

            SwitchHolder mholder= (SwitchHolder) holder;
            final int mposition=startPosition+position;
            mholder.textview.setText(header[mposition]);
            if (position==header.length-1)
                mholder.mSwitch.setChecked(isbootstart);
            else mholder.mSwitch.setChecked(data[mposition%data.length].equals("1"));
            mholder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mposition==header.length-1)
                        isbootstart=isChecked;
                    else  data[mposition]=isChecked?"1":"0";
                }
            });
        }
        }

        @Override
        public int getItemCount() {
            return size;
        }
        class  EditHolder extends RecyclerView.ViewHolder{
            TextView editText;
            TextView textview;
            public EditHolder(View itemView) {
                super(itemView);
                editText= (TextView) itemView.findViewById(R.id.item_edit);
                textview= (TextView) itemView.findViewById(R.id.item_header);
             }
        }
        class  SwitchHolder extends RecyclerView.ViewHolder{

            TextView textview;
            Switch mSwitch;
            public SwitchHolder(View itemView) {
                super(itemView);
                mSwitch= (Switch) itemView.findViewById(R.id.switch_text);
                textview= (TextView) itemView.findViewById(R.id.header_text);
             }
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
        private  int[] type=mConfigTool.getType();
        private String[] header=mConfigTool.getHeader();

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
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity1.this, android.R.layout.simple_spinner_item, items[0]);
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
                               if (type!=2)
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

        }
    }

}
