package com.example.hzg.mysussr.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hzg.mysussr.R;
import com.example.hzg.mysussr.adapter.UidAdapter;
import com.example.hzg.mysussr.application.MyApplication;
import com.example.hzg.mysussr.bean.AppUidBean;
import com.example.hzg.mysussr.utils.Utils;
import com.mobeta.android.dslv.DragSortListView;

import org.w3c.dom.ls.LSException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hzg on 2017/5/10.
 */

public class UidFragment extends Fragment implements  UidAdapter.onUidListener{
    Unbinder mUbinder;
    @BindView(R.id.fragment_uid_list_dslv)
    DragSortListView mDragSortListView;
    @BindView(R.id.fragment_uid_list_srfl)
    SwipeRefreshLayout mSwipeRefereshlayout;

    UidAdapter mAdapter;
    ArrayList<AppUidBean> mDatas;
    ArrayList<String> uidSelectList;

    public static UidFragment newUidFragment(String type)
    {
        Bundle bundle=new Bundle();
        bundle.putString("type",type);
        UidFragment uidFragment=new UidFragment() ;
        uidFragment.setArguments(bundle);
        return  uidFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //复制AppUid数据，避免3个UidFragment相互影响。
        ArrayList<AppUidBean> list=( (MyApplication)getContext().getApplicationContext()).getAppUidList();
        mDatas=new ArrayList<>();
        for (AppUidBean bean:list)
        {
           mDatas.add( bean.clone());
        }
        uidSelectList= Utils.getSelectedList(getContext(),getArguments().getString("type"));
        Log.d("UidFragment","onCreate"+mDatas.hashCode());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_uid_list,container,false);
        mUbinder=ButterKnife.bind(this,view);
        mSwipeRefereshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refreshData();
                mSwipeRefereshlayout.setRefreshing(false);
                Toast.makeText(getContext(),"选中数据已置顶",Toast.LENGTH_SHORT).show();
            }
        });

        mDragSortListView= (DragSortListView) view.findViewById(R.id.fragment_uid_list_dslv);
        mAdapter=new UidAdapter(getActivity(),R.layout.uid_selecte_list,mDatas);
        mAdapter.setListener(this);
        mAdapter.initSelected(uidSelectList);
        mDragSortListView.setAdapter(mAdapter);
        mDragSortListView.setDragSortListener(new DragSortListView.DragSortListener() {
            @Override
            public void drag(int from, int to) {

            }

            @Override
            public void drop(int from, int to) {
                  mAdapter.drop(from,to);
                  canRefresh(true);
            }

            @Override
            public void remove(int which, View view) {

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void canRefresh(boolean canRefresh) {
        mSwipeRefereshlayout.setEnabled(canRefresh);
    }
}
