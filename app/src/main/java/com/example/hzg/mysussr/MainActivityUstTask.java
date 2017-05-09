package com.example.hzg.mysussr;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 2017-04-16 新方法
 */
public class MainActivityUstTask extends AppCompatActivity implements View.OnClickListener {
    RecyclerView applistview;
    FloatingActionButton btnIp;
    private String dataPath = StartAct.sussrPath + "/datalist";
    MyAdapter adapter;
    //储存配置项的容器，配置项为String[] 数组
    ArrayList<String[]> datalist;
    //记录当前配置项在容器的序号
    private int position;
    //记录是否开机启动脚本的变量
    private boolean isbootstart = false;
    private ConfigTool mConfigTool;
    private DownloadBroadcastReceiver downloadBroadcastReceiver;
    private NavigationView navigationView;
    private MenuTool mMenuTool;
    private ArrayList<FloatingActionButton> imageButtons = new ArrayList<>();
    private boolean mFlags = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //弹出菜单
        FloatingActionButton start = (FloatingActionButton) findViewById(R.id.menu_start);
        FloatingActionButton stop = (FloatingActionButton) findViewById(R.id.menu_stop);
        FloatingActionButton check = (FloatingActionButton) findViewById(R.id.menu_check);
        FloatingActionButton ip = (FloatingActionButton) findViewById(R.id.menu_ip);
        ip.setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        check.setOnClickListener(this);
        imageButtons.add(stop);
        imageButtons.add(check);
        imageButtons.add(start);
        imageButtons.add(ip);
        applistview = (RecyclerView) findViewById(R.id.applistview);
        btnIp = (FloatingActionButton) findViewById(R.id.checkip_btn);
        //        btncheck = (Button) findViewById(R.id.check_btn);
////        btnstart = (Button) findViewById(R.id.start_btn);
////        btnstop = (Button) findViewById(R.id.stop_btn);
//        btnstart.setOnClickListener(this);
//        btnstop.setOnClickListener(this);
//        btncheck.setOnClickListener(this);
        btnIp.setOnClickListener(this);
        navigationView = (NavigationView) findViewById(R.id.main_navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
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
                    case R.id.menu_update:
                        mMenuTool.menuUpdate();
                        break;
                }
                return true;
            }
        });
        //
        initParams();
        applistview.setLayoutManager(new LinearLayoutManager(this));
        if (position >= datalist.size()) position = 0;
        adapter = new MyAdapter(datalist, position);
        applistview.setAdapter(adapter);

    }

    private void initParams() {
        SharedPreferences preferences = getSharedPreferences("sussr", MODE_PRIVATE);
        position = preferences.getInt("position", 0);
        isbootstart = preferences.getBoolean("boot", false);
        if (Build.VERSION.SDK_INT > 21 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        mConfigTool = new ConfigTool(dataPath, position);
        downloadBroadcastReceiver = new DownloadBroadcastReceiver();
        registerReceiver(downloadBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        mMenuTool = new MenuTool(this, mConfigTool, downloadBroadcastReceiver);
        datalist = mConfigTool.getDatalist();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
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
        SharedPreferences preferences = getSharedPreferences("sussr", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("position", position);
        editor.putBoolean("boot", isbootstart);
        editor.apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShellTool.ShellTask.relase();
        unregisterReceiver(downloadBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.setGroupVisible(R.id.menu_showAlways, true);
        menu.setGroupVisible(R.id.mene_showNever, false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case  R.id.menu_install_sussr:
//
//                ShellTool.execShellTask(this,mConfigTool.getInstallShell(),true,true);
//                break;
//            case  R.id.menu_install_busybox:
//               Utils.installApk(this,StartAct.BusyboxInstallPath,"com.example.hzg.mysussr.provider");
//                break;
//            case  R.id.menu_uninstall_sussr:
//                ShellTool.execShellTask(this,mConfigTool.getRemoveShell(),true,true);
//                break;
//            case  R.id.menu_reset:
//                SharedPreferences.Editor editor=getSharedPreferences("sussr",MODE_PRIVATE).edit();
//                editor.putBoolean("isaccept",false);
//                editor.commit();
//                Intent intent1=new Intent(this,StartAct.class);
//                startActivity(intent1);
//                finish();
//                break;
            case R.id.menu_select_list:
                selectConfigList();
                break;
            case R.id.menu_edit_setting:
                if (Build.VERSION.SDK_INT > 19) {
                    ShellTool.editTextFileWithShellandStream(this, StartAct.sussrPath + "/temp", "/data/sussr/setting.ini");
                } else FileTool.editTextFileWithStream(this, "/data/sussr/setting.ini");

                break;
//            case  R.id.menu_help:
//                if (Build.VERSION.SDK_INT>19) {
//                    ShellTool.editTextFileWithShellandStream(this,StartAct.sussrPath+"/temp" ,"/data/sussr/说明.txt");
//                }
//                else  FileTool.editTextFileWithStream(this,"/data/sussr/说明.txt");
//                break;
//            case  R.id.menu_update:
//                UpdateTool.checkUpdate(new UpdateTool.CheckCallBack() {
//                    @Override
//                    public void onSuccess(final UpdateAppInfo updateAppInfo) {
//                        AlertDialog.Builder updateBuilder = new AlertDialog.Builder(MainActivityUstTask.this);
//                        updateBuilder.setTitle("当前版本" + UpdateTool.getAppVersionName(getApplicationContext()));
//                        if (updateAppInfo.data.apkVersionCode > UpdateTool.getAppVersionCode(getApplicationContext())) {
//
//                            updateBuilder.setMessage("有新版本可以更新!\n" + updateAppInfo.data.toString());
//                            final String apkFileName = updateAppInfo.data.apkName + updateAppInfo.data.apkVersion + ".apk";
//                            updateBuilder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //检查目录中有没有更新文件，有就直接安装，没有就使用DownloadManger下载
//                                    File apkFile = new File(getExternalFilesDir("apk") + "/" + apkFileName);
//                                    if (apkFile.exists()) {
//                                        Utils.installApk(MainActivityUstTask.this, apkFile.getPath(), "com.example.hzg.mysussr.provider");
//                                    } else {
//                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateAppInfo.data.apkUrl));
//                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//                                        request.setTitle(updateAppInfo.data.apkName + updateAppInfo.data.apkVersion);
//                                        request.setDescription("MySussr更新中");
//                                        request.setMimeType("application/vnd.android.package-archive");
//                                        request.setDestinationInExternalFilesDir(MainActivityUstTask.this, "apk", apkFileName);
//                                        //DownloadManger 下载的文件重名不会覆写，只会在文件名后加一些标识符
//                                        // 如 update-1.apk ，-2
//                                        //需要清理重复的文件
//                                        File apkdir = getExternalFilesDir("apk");
//                                        if (apkdir.exists()) {
//                                            File[] files = apkdir.listFiles();
//                                            for (File file : files) {
//                                                file.delete();
//                                            }
//                                        }
//                                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                                        long id = downloadManager.enqueue(request);
//                                        downloadBroadcastReceiver.setmDownloadId(id);
//                                    }
//                                }
//                            });
//                            updateBuilder.setNeutralButton("暂不更新", null);
//                        } else {
//                            updateBuilder.setMessage("当前版本已经是最新的了，无需更新");
//
//                        }
//
//                        updateBuilder.create().show();
//                    }
//
//
//                    @Override
//                    public void onError() {
//                        Toast.makeText(MainActivityUstTask.this, "访问失败", Toast.LENGTH_LONG).show();
//                    }
//                });
//                break;
//            case R.id.menu_uid:
//                showUidDialog();
//                break;

        }
        return true;
    }

    public void showUidDialog() {
        final ArrayList<Utils.AppUidMessage> uidData = (ArrayList<Utils.AppUidMessage>) Utils.getUidList(this);
        final AlertDialog.Builder uidBuilder = new AlertDialog.Builder(this);
        uidBuilder.setTitle("查看uid");
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uid, parent, false);

                return new UidHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                UidHolder uidHolder = (UidHolder) holder;
                uidHolder.uid.setText(uidData.get(position).getUid());
                uidHolder.label.setText(uidData.get(position).getAppName());
                uidHolder.icon.setImageDrawable(uidData.get(position).getAppIcon());
            }

            @Override
            public int getItemCount() {
                return uidData.size();
            }

            class UidHolder extends RecyclerView.ViewHolder {
                private TextView label;
                private TextView uid;
                private ImageView icon;

                public UidHolder(View itemView) {
                    super(itemView);
                    label = (TextView) itemView.findViewById(R.id.uid_label);
                    uid = (TextView) itemView.findViewById(R.id.uid_uid);
                    icon = (ImageView) itemView.findViewById(R.id.uid_icon);
                }
            }
        });
        uidBuilder.setView(recyclerView);
        uidBuilder.setPositiveButton("关闭", null);
        uidBuilder.create().show();
    }

    public void selectConfigList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择配置");
        String[] items = new String[datalist.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = datalist.get(i)[0];
        }
        final int[] select = {position};
        builder.setSingleChoiceItems(items, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select[0] = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (position != select[0]) position = select[0];
                adapter.setDatePosition(position);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (datalist.size() > 1) {
                    datalist.remove(select[0]);
                    if (position == select[0]) {
                        position = 0;
                        adapter.setDatePosition(position);
                        adapter.notifyDataSetChanged();
                    } else if (position > select[0]) {
                        position--;
                    }
                }
                selectConfigList();
            }
        });
        builder.setNegativeButton("新建", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivityUstTask.this);
                builder1.setTitle("配置名称");
                final EditText inputText = new EditText(MainActivityUstTask.this);
                inputText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                builder1.setPositiveButton("新建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] defaultSetting = mConfigTool.getDefaultConfigItem(inputText.getText().toString());
                        datalist.add(defaultSetting);
                        position = datalist.size() - 1;
                        adapter.setDatePosition(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder1.setNeutralButton("导入", null);
                builder1.setNegativeButton("取消", null);
                builder1.setView(inputText);
                final AlertDialog dialogCreate = builder1.show();
                //点击按钮，错误窗口不消失
                dialogCreate.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ssr = inputText.getText().toString();
                        if (ssr.indexOf("ssr://") == 0) {
                            String[] defaultSetting = mConfigTool.getConfigItemFromSSR(ssr);
                            if (defaultSetting == null) {
                                inputText.setError("导入失败,请输入正确的ssr链接");
                                //Toast.makeText(MainActivityUstTask.this, "导入失败,请输入正确的ssr链接", Toast.LENGTH_LONG).show();
                            } else {
                                datalist.add(defaultSetting);
                                position = datalist.size() - 1;
                                adapter.setDatePosition(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MainActivityUstTask.this, "导入成功", Toast.LENGTH_LONG).show();
                                dialogCreate.dismiss();

                            }
                        } else {
                            inputText.setError("请输入正确的ssr链接");
                            //Toast.makeText(MainActivityUstTask.this, "请输入正确的ssr链接", Toast.LENGTH_LONG).show();

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
        switch (v.getId()) {
            case R.id.check_btn:
                ShellTool.execShellTask(this, mConfigTool.getCheckShell(), true, true);
                break;
            case R.id.start_btn:
                ShellTool.execShellTask(this, mConfigTool.getStartShell(position), true, false);
                break;
            case R.id.stop_btn:
                ShellTool.execShellTask(this, mConfigTool.getStopShell(), true, true);
                break;
            case  R.id.menu_start:
                ShellTool.execShellTask(this, mConfigTool.getStartShell(position), true, false);
                break;
            case R.id.menu_stop:
                ShellTool.execShellTask(this, mConfigTool.getStopShell(), true, true);
                break;
            case R.id.menu_check:
                ShellTool.execShellTask(this, mConfigTool.getCheckShell(), true, true);
                break;
            case R.id.menu_ip:
                showIpDialog();
                break;
            case R.id.checkip_btn:
                if (mFlags)
                    startAnim();
                else closeAnim();
                break;


        }
    }

    private void showIpDialog() {
        final WebView webview = new WebView(this);
        webview.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        webview.loadUrl("http://m.ip138.com/");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webview.loadUrl(url);
                return true;
            }
        });
        AlertDialog.Builder webviewBuilder = new AlertDialog.Builder(this);
        webviewBuilder.setView(webview);
        webviewBuilder.setNegativeButton("关闭", null);
        webviewBuilder.show();
    }

    private void startAnim() {
        for (FloatingActionButton f : imageButtons) {
            f.setVisibility(View.VISIBLE);
            f.setClickable(true);
        }
        float  dimen=Utils.dp2px(getApplicationContext(),62);
        ObjectAnimator animator = ObjectAnimator.ofFloat(btnIp, "alpha", 1f, 0.5f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(imageButtons.get(0), "translationY", dimen);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(imageButtons.get(1), "translationY", -dimen);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(imageButtons.get(2), "translationX", -dimen);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(imageButtons.get(3), "translationX", dimen);

        ObjectAnimator animator5 = ObjectAnimator.ofFloat(imageButtons.get(0), "alpha", 1f);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(imageButtons.get(1), "alpha", 1f);
        ObjectAnimator animator7 = ObjectAnimator.ofFloat(imageButtons.get(2), "alpha", 1f);
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(imageButtons.get(3), "alpha", 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.playTogether(animator, animator1, animator2, animator3, animator4
                , animator5, animator6, animator7, animator8);
        animatorSet.start();
        mFlags = false;
    }

    private void closeAnim() {
        for (FloatingActionButton f : imageButtons) {
            f.setClickable(false);
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(btnIp, "alpha", 1f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(imageButtons.get(0), "translationY", 0f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(imageButtons.get(1), "translationY", 0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(imageButtons.get(2), "translationX", 0f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(imageButtons.get(3), "translationX", 0f);

        ObjectAnimator animator5 = ObjectAnimator.ofFloat(imageButtons.get(0), "alpha", 0f);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(imageButtons.get(1), "alpha", 0f);
        ObjectAnimator animator7 = ObjectAnimator.ofFloat(imageButtons.get(2), "alpha", 0f);
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(imageButtons.get(3), "alpha", 0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new DecelerateInterpolator());

        animatorSet.playTogether(animator, animator1, animator2, animator3, animator4
                , animator5, animator6, animator7, animator8);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                for (FloatingActionButton f : imageButtons) {
                    f.setVisibility(View.GONE);
                }
            }
        });

        mFlags = true;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;
        EditText contentText;
        Switch switchText;

        public MyViewHolder(View itemView) {
            super(itemView);
            headerText = (TextView) itemView.findViewById(R.id.header_text);
            contentText = (EditText) itemView.findViewById(R.id.content_text);
            switchText = (Switch) itemView.findViewById(R.id.switch_text);

        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<String[]> datalist;
        private String[] data;
        /*0 edittext             隐藏开关按钮          弹出窗口为编辑框
        /*1 edittext（隐藏密码） 隐藏开关按钮          弹出窗口为编辑框
        /*2 edittext             隐藏开关按钮          弹出窗口为选项表
        /*3 edittext(无文字)     开关按钮              无弹出窗口
        /*4 edittext(有文字)     开关按钮              弹出窗口为编辑框
        * */
        private int[] type = mConfigTool.getType();
        private String[] header = mConfigTool.getHeader();

        public MyAdapter(ArrayList<String[]> datalist, int position) {
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
            Log.d("onBindViewHolder", "start;position:" + position);
            holder.headerText.setText(header[position]);
            switch (type[position]) {
                case 0:
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.switchText.setVisibility(View.GONE);
                    holder.contentText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    holder.contentText.setText(data[position]);
                    holder.contentText.setOnClickListener(getListener(holder, position, 0));
                    break;
                case 1:
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.switchText.setVisibility(View.GONE);
                    holder.contentText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    holder.contentText.setText(data[position]);
                    holder.contentText.setOnClickListener(getListener(holder, position, 1));
                    break;
                case 2:
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.switchText.setVisibility(View.GONE);
                    holder.contentText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    holder.contentText.setText(data[position]);
                    holder.contentText.setOnClickListener(getListener(holder, position, 2));
                    break;
                case 3:
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.switchText.setVisibility(View.VISIBLE);
                    holder.contentText.setText("");
                    holder.contentText.setOnClickListener(getListener(holder, position, 3));
                    //recycleview 对switch复用会导致switch里的监听器
                    holder.switchText.setOnCheckedChangeListener(null);
                    //开机自启开关位于列表最后一项
                    if (position == header.length - 1) {
                        Log.d("BootStart", "" + isbootstart + "position:" + position);
                        holder.switchText.setChecked(isbootstart);
                    } else {
                        if (data[position].equals("1"))
                            holder.switchText.setChecked(true);
                        else holder.switchText.setChecked(false);
                    }
                    holder.switchText.setOnCheckedChangeListener(getSwitchListener(holder, position, 3));
                    break;
                case 4:
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.switchText.setVisibility(View.VISIBLE);
                    holder.contentText.setText(data[position]);
                    holder.contentText.setOnClickListener(getListener(holder, position, 4));
                    //recycleview 对switch复用会导致switch里的监听器
                    holder.switchText.setOnCheckedChangeListener(null);
                    //开机自启开关位于列表最后一项
                    if (position == header.length - 1) {
                        Log.d("BootStart", "" + isbootstart + "position:" + position);
                        holder.switchText.setChecked(isbootstart);
                    } else {
                        if (data[position].equals("1"))
                            holder.switchText.setChecked(true);
                        else holder.switchText.setChecked(false);
                    }
                    holder.switchText.setOnCheckedChangeListener(getSwitchListener(holder, position, 4));
                    break;
            }


        }

        @NonNull
        public CompoundButton.OnCheckedChangeListener getSwitchListener(final MyViewHolder holder, final int position, final int type) {
            return new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (position == header.length - 1) {
                        Log.d("switchOnChecked", "isboot:" + isbootstart);
                        isbootstart = isChecked;
                        Log.d("switchOnChecked", position + ":" + isChecked + ":" + isbootstart);
                    } else {
                        if (isChecked) data[position] = "1";
                        else data[position] = "0";
                    }
                    if (type == 4) holder.contentText.setText(data[position]);

                }
            };
        }

        @Nullable
        public View.OnClickListener getListener(final MyViewHolder holder, final int position, final int type) {
            View.OnClickListener listener = null;
            if (type != 3) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击弹出dialog窗口
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle(header[position]);
                        View root = null;
                        //列表5-7 窗口内容为spinner
                        if (type == 2) {
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
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivityUstTask.this, android.R.layout.simple_spinner_item, items[0]);
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
                            root = spinner;

                        } else {
                            //其他列表项 窗口内容为EditText
                            EditText editText = new EditText(v.getContext());
                            editText.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            editText.setText(data[position]);
                            editText.setSelection(data[position].length());
                            root = editText;
                        }

                        builder.setNegativeButton("取消", null);
                        final View finalRoot = root;
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (type != 2)
                                    data[position] = ((EditText) finalRoot).getText().toString().trim();

                                holder.contentText.setText(data[position]);

                            }
                        });
                        builder.setView(root);
                        builder.show();
                        if (type != 2) { //延时弹出软键盘
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
            data = datalist.get(position);

        }
    }

}
