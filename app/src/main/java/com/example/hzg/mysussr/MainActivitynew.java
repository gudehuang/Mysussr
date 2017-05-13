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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.hzg.mysussr.adapter.ConfigAdapter;
import com.example.hzg.mysussr.bean.ConfigSelectBean;
import com.example.hzg.mysussr.listener.ConfigListener;
import com.example.hzg.mysussr.receiver.DownloadBroadcastReceiver;
import com.example.hzg.mysussr.utils.FileTool;
import com.example.hzg.mysussr.utils.ShellTool;
import com.example.hzg.mysussr.view.ConfigListDialog;
import com.example.hzg.mysussr.view.ConfigListDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Create By hzg
 */

public class MainActivitynew extends AppCompatActivity {
    @BindView(R.id.applistview)
    RecyclerView applistview;
    @BindView(R.id.checkip_btn)
    FloatingActionButton actionButton;
    @BindViews({R.id.menu_stop,R.id.menu_start, R.id.menu_check, R.id.menu_ip})
    List<FloatingActionButton> popButtons = new ArrayList<>();
    @BindView(R.id.main_navigation)
    NavigationView navigationView;
    private String dataPath = StartAct.sussrPath + "/datalist";
    ConfigAdapter adapter;
    //储存配置项的容器，配置项为String[] 数组
    ArrayList<String[]> datalist;
    //记录当前配置项在容器的序号
    private int position;
    //记录是否开机启动脚本的变量
    private boolean isbootstart = false;
    private ConfigToolnew mConfigTool;
    private DownloadBroadcastReceiver downloadBroadcastReceiver;

    private MenuToolnew mMenuTool;

    private boolean mFlags = true;
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        /**
         * 获取uid信息
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.example.hzg.mysussr.utils.Utils.initUidMsg(MainActivitynew.this);
            }
        }).start();
        initParams();

        /**
         * 为开机自启设置选择按钮
         */

        Switch mSwitch = new Switch(MainActivitynew.this);
        mSwitch.setChecked(isbootstart);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isbootstart = isChecked;
                System.out.println(isbootstart);
            }
        });
        navigationView.getMenu().findItem(R.id.menu_boot).setActionView(mSwitch);

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
                        mMenuTool.menuUid(MainActivitynew.this);
                        break;
                    case R.id.menu_help:
                        mMenuTool.menuHlep();
                        break;
                    case R.id.menu_reset:
                        mMenuTool.menuReLoad(MainActivitynew.this);
                        break;
                    case R.id.menu_update:
                        mMenuTool.menuUpdate();
                        break;
                    case R.id.menu_boot:
                        break;
                }
                return true;
            }
        });
        applistview.setLayoutManager(new LinearLayoutManager(this));
        if (position >= datalist.size()) position = 0;
        adapter = new ConfigAdapter(datalist, mConfigTool.getHeader(), position);
        adapter.setListener(new ConfigListener(this, adapter));
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
        mConfigTool = new ConfigToolnew(this, dataPath, position);
        downloadBroadcastReceiver = new DownloadBroadcastReceiver();
        registerReceiver(downloadBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        mMenuTool = new MenuToolnew(this, mConfigTool, downloadBroadcastReceiver);
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
        getSharedPreferences("sussr", MODE_PRIVATE)
                .edit()
                .putInt("position", position)
                .putBoolean("boot", isbootstart)
                .apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadBroadcastReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //双击退出Activity
    private void exit() {
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
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

            case R.id.menu_select_list:
                //selectConfigList();
                shoeSelectDialog();
               // showDialog();

                break;
            case R.id.menu_edit_setting:

                if (Build.VERSION.SDK_INT > 19) {
                    ShellTool.editTextFileWithShellandStream(this, StartAct.sussrPath + "/temp", "/data/sussr/setting.ini");
                } else FileTool.editTextFileWithStream(this, "/data/sussr/setting.ini");

                break;

        }
        return true;
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivitynew.this);
                builder1.setTitle("配置名称");
                final EditText inputText = new EditText(MainActivitynew.this);
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
                                Toast.makeText(MainActivitynew.this, "导入成功", Toast.LENGTH_LONG).show();
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

     public  void shoeSelectDialog()
     {
         ArrayList<ConfigSelectBean> data=new ArrayList<>();
         for (int i=0;i<datalist.size();i++)
         {
             data.add(new ConfigSelectBean(datalist.get(i)[0],i));
         }
         ConfigListDialogFragment fragDialog= ConfigListDialogFragment.newConfigListFragDialog(this,data,position);
        fragDialog.setListener(new ConfigListDialogFragment.onSelectedListener() {
            @Override
            public void selected(int position1) {
                position=position1;
                adapter.setDatePosition(position);
            }

            @Override
            public void createConfigItem(String configname) {
                datalist.add(mConfigTool.getDefaultConfigItem(configname));
                position=datalist.size()-1;
                adapter.setDatePosition(position);
            }

            @Override
            public boolean createConfigItemFromSSR(String ssr) {
                String[] item=mConfigTool.getConfigItemFromSSR(ssr);
                if (item!=null)
                {
                    datalist.add(item);
                    position=datalist.size()-1;
                    adapter.setDatePosition(datalist.size()-1);
                    return  true;
                }
                return false;
            }

            @Override
            public void drop(int from, int to) {
                String[] item=datalist.get(from);
                datalist.remove(from);
                datalist.add(to,item);
            }

            @Override
            public void remove(int position) {
                datalist.remove(position);
            }
        });
         fragDialog.show(getSupportFragmentManager(),"frag");
     }
     public  void showDialog()
     {
         ArrayList<ConfigSelectBean> data=new ArrayList<>();
         for (int i=0;i<datalist.size();i++)
         {
             data.add(new ConfigSelectBean(datalist.get(i)[0],i));
         }
         ConfigListDialog dialog=new ConfigListDialog(this, data, position,new ConfigListDialog.onSelectedListener() {
             @Override
             public void selected(int position1) {
                 position=position1;
                 adapter.setDatePosition(position);
             }

             @Override
             public void createConfigItem(String configname) {
                 datalist.add(mConfigTool.getDefaultConfigItem(configname));
                 adapter.setDatePosition(datalist.size()-1);
             }

             @Override
             public boolean createConfigItemFromSSR(String ssr) {
                 String[] item=mConfigTool.getConfigItemFromSSR(ssr);
                 if (item!=null)
                 {
                     datalist.add(item);
                     adapter.setDatePosition(datalist.size()-1);
                     return  true;
                 }
                 return false;
             }

             @Override
             public void drop(int from, int to) {
                    String[] item=datalist.get(from);
                    datalist.remove(from);
                    datalist.add(to,item);
             }

             @Override
             public void remove(int position) {
                     datalist.remove(position);
             }
         });
         dialog.show();
     }

    @OnClick({R.id.menu_start, R.id.menu_check, R.id.menu_stop, R.id.menu_ip, R.id.checkip_btn})
    public void onPopMenuClick(View v) {
        switch (v.getId()) {
            case R.id.menu_check:
                ShellTool.execShellTask(this, mConfigTool.getCheckShell(), true, true);
                break;
            case R.id.menu_start:
                ShellTool.execShellTask(this, mConfigTool.getStartShell(this, position), true, false);
                break;
            case R.id.menu_stop:
                ShellTool.execShellTask(this, mConfigTool.getStopShell(), true, true);
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
        for (FloatingActionButton f : popButtons) {
            f.setVisibility(View.VISIBLE);
            f.setClickable(true);
        }
        float dimen = Utils.dp2px(getApplicationContext(), 62);
        ObjectAnimator animator = ObjectAnimator.ofFloat(actionButton, "alpha", 1f, 0.5f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(popButtons.get(0), "translationY", dimen);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(popButtons.get(1), "translationY", -dimen);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(popButtons.get(2), "translationX", -dimen);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(popButtons.get(3), "translationX", dimen);

        ObjectAnimator animator5 = ObjectAnimator.ofFloat(popButtons.get(0), "alpha", 1f);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(popButtons.get(1), "alpha", 1f);
        ObjectAnimator animator7 = ObjectAnimator.ofFloat(popButtons.get(2), "alpha", 1f);
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(popButtons.get(3), "alpha", 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.playTogether(animator, animator1, animator2, animator3, animator4
                , animator5, animator6, animator7, animator8);
        animatorSet.start();
        mFlags = false;
    }

    private void closeAnim() {
        for (FloatingActionButton f : popButtons) {
            f.setClickable(false);
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(actionButton, "alpha", 1f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(popButtons.get(0), "translationY", 0f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(popButtons.get(1), "translationY", 0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(popButtons.get(2), "translationX", 0f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(popButtons.get(3), "translationX", 0f);

        ObjectAnimator animator5 = ObjectAnimator.ofFloat(popButtons.get(0), "alpha", 0f);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(popButtons.get(1), "alpha", 0f);
        ObjectAnimator animator7 = ObjectAnimator.ofFloat(popButtons.get(2), "alpha", 0f);
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(popButtons.get(3), "alpha", 0f);
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
                for (FloatingActionButton f : popButtons) {
                    f.setVisibility(View.GONE);
                }
            }
        });

        mFlags = true;
    }


}
