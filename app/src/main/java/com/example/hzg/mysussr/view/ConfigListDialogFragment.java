package com.example.hzg.mysussr.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hzg.mysussr.R;
import com.example.hzg.mysussr.adapter.ConfigSelectAdapter;
import com.example.hzg.mysussr.bean.ConfigSelectBean;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;

/**
 * Created by hzg on 2017/5/13.
 */

public class ConfigListDialogFragment extends DialogFragment {
    ArrayList<ConfigSelectBean> mDatas;
    Context mContext;
    onSelectedListener listener;
    ConfigSelectAdapter adapter;
    DragSortListView mDragSortListView;
    Button saveBtn;
    Button creatBtn;

    int mPosition;

    public static ConfigListDialogFragment newConfigListFragDialog(Context context, ArrayList<ConfigSelectBean> datas, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", datas);
        bundle.putInt("position", position);
        ConfigListDialogFragment fragDialog = new ConfigListDialogFragment();
        fragDialog.setArguments(bundle);
        return fragDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mDatas = (ArrayList<ConfigSelectBean>) getArguments().getSerializable("data");
        mPosition = getArguments().getInt("position");
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_configlist, container, false);
        mDragSortListView = (DragSortListView) view.findViewById(R.id.dialog_configlist_dslv);
        saveBtn = (Button) view.findViewById(R.id.dialog_configlist_ok);
        creatBtn = (Button) view.findViewById(R.id.dialog_configlist_new);
        mDragSortListView = (DragSortListView) view.findViewById(R.id.dialog_configlist_dslv);
        saveBtn = (Button) view.findViewById(R.id.dialog_configlist_ok);
        creatBtn = (Button) view.findViewById(R.id.dialog_configlist_new);
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
                    dismiss();
                }

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
                            dismiss();
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
                                    dismiss();
                                    Toast.makeText(mContext, "导入成功", Toast.LENGTH_LONG).show();
                                    dialogCreate.dismiss();
                                }
                            }


                        } else {
                            inputText.setError("请输入正确的ssr链接");
                        }
                    }

                });
            }
        });
        return view;
    }

    public void setListener(onSelectedListener listener) {
        this.listener = listener;
    }

    public interface onSelectedListener {
        void selected(int position);

        void createConfigItem(String configname);

        boolean createConfigItemFromSSR(String ssr);

        void drop(int from, int to);

        void remove(int position);
    }
}
