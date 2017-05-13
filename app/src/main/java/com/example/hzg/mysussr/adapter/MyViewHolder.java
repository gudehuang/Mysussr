package com.example.hzg.mysussr.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by hzg on 2017/5/11.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
    String TAG=this.getClass().getSimpleName();
    private SparseArray<View> mViews;
    public MyViewHolder(View itemView) {
        super(itemView);
        mViews=new SparseArray<>();
        Log.d(TAG,"MyViewHolder create");
    }
    public  <T extends View> T getView(int viewId)
    {
        View view=mViews.get(viewId);
        if (view==null)
        {
            view=itemView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return  (T)view;
    }
}
