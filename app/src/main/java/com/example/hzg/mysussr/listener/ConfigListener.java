package com.example.hzg.mysussr.listener;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.hzg.mysussr.R;
import com.example.hzg.mysussr.adapter.ConfigAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by hzg on 2017/5/12.
 */
public class ConfigListener implements OnViewHolderItemClickListener {
    ConfigAdapter adapter;
    Context mContext;

    public ConfigListener(Context context,ConfigAdapter adapter) {
        this.adapter = adapter;
        this.mContext = context;
    }

    @Override
    public void onClick(final int position, String data) {
        Log.d("", "onClick position:" + position + " data:" + data);
        //点击弹出dialog窗口
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(adapter.getHeader()[position]);
        View root = null;
        //列表5-7 窗口内容为spinner
        if (position > 4 && position < 8) {
            Spinner spinner = new Spinner(mContext);
            spinner.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            spinner.setPadding(16, 0, 16, 0);
            final String[][] items = new String[][]{{""}};
            switch (position) {
                case 5:
                    items[0] = mContext.getResources().getStringArray(R.array.method);
                    break;
                case 6:
                    items[0] = mContext.getResources().getStringArray(R.array.protocol);
                    break;
                case 7:
                    items[0] = mContext.getResources().getStringArray(R.array.obscure);
                    break;
            }
            ArrayList<String> item = new ArrayList<String>(Arrays.asList(items[0]));
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, items[0]);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
            spinner.setSelection(item.indexOf(data));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                    adapter.getData()[position] = items[0][position1];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            root = spinner;

        } else {
            //其他列表项 窗口内容为EditText
            EditText editText = new EditText(mContext);
            editText.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            editText.setText(data);
            editText.setSelection(data.length());
            root = editText;
        }

        builder.setNegativeButton("取消", null);
        final View finalRoot = root;
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!(position > 4 && position < 8))
                    adapter.getData()[position] = ((EditText) finalRoot).getText().toString().trim();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setView(root);
        builder.show();
        if (!(position > 4 && position < 8)) { //延时弹出软键盘
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

    @Override
    public void onCheckedChange(int position, boolean isChecked) {
        adapter.getData()[position] = isChecked ? "1" : "0";
    }
}
