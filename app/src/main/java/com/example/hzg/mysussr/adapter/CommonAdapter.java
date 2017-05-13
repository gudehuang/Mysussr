package com.example.hzg.mysussr.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by hzg on 2017/5/9.
 */

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected List<T> mDatas;
    protected Context mContext;
    protected  final  int mItemLayoutId;
    public CommonAdapter(Context context, int itemLayoutId, List<T> mDatas) {
        this.mContext=context;
        this.mDatas = mDatas;
        this.mItemLayoutId=itemLayoutId;
    }

    @Override
    public int getCount() {
        if (mDatas==null)
        return 0;
        return  mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=getViewHolder(position,convertView,parent);
        convert(viewHolder,position,mDatas.get(position));
        return viewHolder.getConvertView();
    }
    public  abstract  void  convert(ViewHolder helper,int position, T item);
    protected ViewHolder getViewHolder(int position, View convertView, ViewGroup parent)
    {
        return  ViewHolder.get(mContext,convertView,parent,mItemLayoutId,position);
    }
    public void  remove(int position)
    {
        mDatas.remove(position);
        notifyDataSetChanged();
    }
    public  void  insert(T item,int position)
    {
        mDatas.add(position,item);
        notifyDataSetChanged();
    }

    public void drop(int from, int to) {
        T item=mDatas.get(from);
        mDatas.remove(from);
        mDatas.add(to,item);

    }
}
