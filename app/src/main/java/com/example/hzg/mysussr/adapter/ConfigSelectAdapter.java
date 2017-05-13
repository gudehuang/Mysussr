package com.example.hzg.mysussr.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.HardwarePropertiesManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hzg.mysussr.R;
import com.example.hzg.mysussr.bean.ConfigSelectBean;

import java.util.List;

/**
 * Created by hzg on 2017/5/12.
 *
 */

public class ConfigSelectAdapter  extends CommonAdapter<ConfigSelectBean>{
    private  int selectPosition;
    private  onClickListener listener;
    public ConfigSelectAdapter(Context context, int itemLayoutId, List mDatas,int selectPosition) {
        super(context, itemLayoutId, mDatas);
        this.selectPosition=selectPosition;
    }

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    @Override
    public void convert(ViewHolder helper, final int position, ConfigSelectBean item) {
        final ImageView selected=helper.getView(R.id.config_selected);
        if (position==selectPosition)
        {
            selected.setSelected(true);
        }
        else selected.setSelected(false);
        selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected.setSelected(!selected.isSelected());
                if (selected.isSelected())
                {
                    selectPosition=position;;
                }
                notifyDataSetChanged();
            }
        });
        TextView congifname=helper.getView(R.id.config_name);
        congifname.setText(item.getName());
        ImageView delect= helper.getView(R.id.dlsv_remove);
        delect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setMessage("确定删除？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDatas.size()>1) {
                            mDatas.remove(position);
                            if (position == selectPosition) selectPosition = 0;
                            else if (position < selectPosition) selectPosition--;
                            if (listener != null)
                                listener.remove(position);
                            notifyDataSetChanged();
                        }
                        else Toast.makeText(mContext,"至少保留一个配置项！",Toast.LENGTH_SHORT).show();
                    }
                });
               // builder.setNegativeButton("取消",null);
                builder.setNeutralButton("取消",null);
                builder.show();
            }
        });
    }



    public  interface  onClickListener{
        void remove(int position);
        void drop(int from,int to);
    }
}
