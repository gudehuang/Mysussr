package com.example.hzg.mysussr.adapter;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hzg.mysussr.R;
import com.example.hzg.mysussr.bean.AppUidBean;

import org.w3c.dom.ls.LSException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzg on 2017/5/10.
 */

public class UidAdapter extends CommonAdapter<AppUidBean> {
    private onUidListener listener;
    public UidAdapter(Context context, int itemLayoutId, List<AppUidBean> mDatas) {
        super(context, itemLayoutId, mDatas);

    }

    public void setListener(onUidListener listener) {
        this.listener = listener;
    }

    @Override
    public void convert(ViewHolder helper, int position, final AppUidBean item) {
        final ImageView imageSelected = helper.getView(R.id.app_selected);
        imageSelected.setSelected(item.isSelected());
        imageSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelected(!item.isSelected());
                imageSelected.setSelected(item.isSelected());
            }
        });

        ImageView appIcon = helper.getView(R.id.app_icon);
        appIcon.setImageDrawable(item.getAppIcon());

        TextView appName=helper.getView(R.id.app_name);
        appName.setText(item.getAppName());
        TextView appUid=helper.getView(R.id.app_uid);
        appUid.setText(item.getUid());
        ImageView drag=helper.getView(R.id.dlsv_drag);
        drag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                       if (listener!=null)
                       {
                           listener.canRefresh(false);
                       }
                        break;
                }
                return false;
            }
        });
    }

    public ArrayList<AppUidBean> getSelectedList()
    {
        ArrayList<AppUidBean> appUidBeen=new ArrayList<>();
        for (AppUidBean bean:mDatas)
        {
            if (bean.isSelected())
            {
                appUidBeen.add(bean);
            }
        }
        return  appUidBeen;
    }
    public ArrayList<String> getSelectedUidList()
    {
        ArrayList<String> appUid=new ArrayList<>();
        for (AppUidBean bean:mDatas)
        {
            if (bean.isSelected())
            {
                appUid.add(bean.getUid());
            }
        }
        return  appUid;
    }
    public void initSelected(ArrayList<String> selectedList)
    {
        for (AppUidBean bean :mDatas)
        {
            if (selectedList.contains(bean.getUid()))
            {
                bean.setSelected(true);
            }
        }
        refreshData();
    }
    public void  selectAll(boolean isSelected)
    {
        for (AppUidBean bean:mDatas)
        {
            bean.setSelected(isSelected);
        }
        notifyDataSetChanged();
    }
    public  void drop(int from,int to)
    {
        AppUidBean bean=mDatas.get(from);
        mDatas.remove(from);
        mDatas.add(to,bean);
        notifyDataSetChanged();
    }
    public  void refreshData()
    {

        Log.d("UidFragment","refreshData"+this.hashCode());
        ArrayList<AppUidBean> appUidBeen=new ArrayList<>();
        //获取以选取的列表
        for (AppUidBean bean:mDatas)
        {
            if (bean.isSelected())
            {
                appUidBeen.add(bean);

            }

        }
        //删除以选取的列表，再从新注入
       mDatas.removeAll(appUidBeen);
        for (AppUidBean bean:appUidBeen)
        {
            mDatas.add(0,bean);
        }
        notifyDataSetChanged();
    }
    public  interface onUidListener{
        void  canRefresh(boolean canRefresh);
    }
}
