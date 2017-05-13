package com.example.hzg.mysussr.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hzg.mysussr.MainActivitynew;
import com.example.hzg.mysussr.R;
import com.example.hzg.mysussr.adapter.ConfigSelectAdapter;
import com.example.hzg.mysussr.bean.ConfigSelectBean;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hzg on 2017/5/12.
 */

public class ConfigListDialog extends AlertDialog {

    int mPosition;

    public ConfigListDialog(@NonNull Context context, ArrayList<ConfigSelectBean> data, int position, onSelectedListener listener) {
        super(context);
        this.mDatas = data;
        this.mContext = context;
        this.listener = listener;
        this.mPosition = position;
    }

    ArrayList<ConfigSelectBean> mDatas;
    Context mContext;
    onSelectedListener listener;
    ConfigSelectAdapter adapter;
    DragSortListView mDragSortListView;

    Button saveBtn;

    Button creatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_configlist);
        setCanceledOnTouchOutside(false);
        mDragSortListView = (DragSortListView) findViewById(R.id.dialog_configlist_dslv);
        saveBtn = (Button) findViewById(R.id.dialog_configlist_ok);
        creatBtn = (Button) findViewById(R.id.dialog_configlist_new);
        adapter = new ConfigSelectAdapter(mContext, R.layout.config_selecte_list_item, mDatas, mPosition);
        adapter.setListener(new ConfigSelectAdapter.onClickListener() {
            @Override
            public void remove(int position) {
                listener.remove(position);
            }

            @Override
            public void drop(int from, int to) {

            }
        });
        mDragSortListView.setAdapter(adapter);
        mDragSortListView.setDragSortListener(new DragSortListView.DragSortListener() {
            @Override
            public void drag(int from, int to) {

            }

            @Override
            public void drop(int from, int to) {
                int selPosition = adapter.getSelectPosition();
                adapter.drop(from, to);
                if (from == selPosition)
                    selPosition = to;
                else if (from < selPosition && to >= selPosition) selPosition = selPosition - 1;
                else if (from > selPosition && to <= selPosition) selPosition = selPosition + 1;
                adapter.setSelectPosition(selPosition);

                adapter.notifyDataSetChanged();
                listener.drop(from, to);
            }

            @Override
            public void remove(int which, View view) {

            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.selected(adapter.getSelectPosition());
                }
                ConfigListDialog.this.dismiss();
            }
        });
        creatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                builder1.setTitle("配置名称");
                final EditText inputText = new EditText(mContext);
                inputText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                builder1.setPositiveButton("新建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.createConfigItem(inputText.getText().toString().trim());
                            ConfigListDialog.this.dismiss();
                        }

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
                            if (listener != null) {
                                boolean isOK = listener.createConfigItemFromSSR(ssr);
                                if (!isOK) {
                                    inputText.setError("导入失败,请输入正确的ssr链接");
                                } else {
                                    Toast.makeText(mContext, "导入成功", Toast.LENGTH_LONG).show();
                                    dialogCreate.dismiss();
                                    ConfigListDialog.this.dismiss();
                                }
                            }


                        } else {
                            inputText.setError("请输入正确的ssr链接");
                        }
                    }

                });
            }
        });
    }

    @Override
    public View onCreatePanelView(int featureId) {
        return super.onCreatePanelView(featureId);
    }

    public interface onSelectedListener {
        void selected(int position);

        void createConfigItem(String configname);

        boolean createConfigItemFromSSR(String ssr);

        void drop(int from, int to);

        void remove(int position);
    }
}
