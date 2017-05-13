package com.example.hzg.mysussr.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by hzg on 2017/5/9.
 *
 */

public class ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private int mPosition;
    private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
       this.mPosition=position;
        this.mViews=new SparseArray<>();
        mConvertView= LayoutInflater.from(context).inflate(layoutId,parent,false);
        mViews.put(layoutId,mConvertView);
        mConvertView.setTag(this);
    }
    public static ViewHolder  get(Context context, View convertView, ViewGroup parent, int layoutId, int position)
    {
        if (convertView==null)
        {
            return new ViewHolder(context,parent,layoutId,position);
        }
        return (ViewHolder) convertView.getTag();
    }
@SuppressWarnings("unchecked")
    public  <T extends View> T getView(int viewId)
    {
        View view=mViews.get(viewId);
        if (view==null)
        {
            view=mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return  (T)view;
    }
    public View getConvertView(int layoutId)
    {
        return  mViews.get(layoutId);

    }
    public View getConvertView()
    {
        return mConvertView;
    }
    public ViewHolder setText(int viewId,String text)
    {
        TextView view=getView(viewId);
        view.setText(text);
        return  this;
    }
}
