package com.example.hzg.mysussr.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hzg.mysussr.R;
import com.example.hzg.mysussr.adapter.MyFragmentPagerAdapter;
import com.example.hzg.mysussr.bean.AppUidBean;
import com.example.hzg.mysussr.utils.Utils;
import com.example.hzg.mysussr.view.MyViewPager;
import com.example.hzg.mysussr.view.TabLinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hzg on 2017/5/10.
 */

public class UidActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.fragment_uid_tablayout)
    TabLinearLayout mTabLinrearLayout;
    @BindView(R.id.fragment_uid_viewpager)
    MyViewPager mViewPager;
    @BindView(R.id.fragment_uid_bottomlayout_selectall)
    TextView selectAll;
    @BindView(R.id.fragment_uid_bottomlayout_save)
    TextView saveSelected;
    MyFragmentPagerAdapter mAdapter;
    Fragment[] fragments = new Fragment[3];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_uid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        saveSelected.setOnClickListener(this);
        selectAll.setOnClickListener(this);
        mTabLinrearLayout.setListener(new TabLinearLayout.TabonClickListener() {
            @Override
            public void onClick(int position) {
                mViewPager.setCurrentItem(position, false);
            }
        });
        mViewPager.setOffscreenPageLimit(fragments.length);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        UidFragment uidFragment = null;
        switch (v.getId()) {
            case R.id.fragment_uid_bottomlayout_save:
                //将当前页面选中的UID信息转为String，分隔符为#，保存到SharePreference中
                uidFragment = mAdapter.getCurrentFragment();
                String path=uidFragment.getArguments().getString("type");
                ArrayList<String> uidlist=uidFragment.mAdapter.getSelectedUidList();
                String uidString= Utils.arrayToString(uidlist,",");
                getSharedPreferences("sussr", MODE_PRIVATE)
                        .edit()
                        .putString(path, uidString)
                        .apply();
                Log.d("save","path:"+path+"  string:"+uidString);
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_uid_bottomlayout_selectall:
                selectAll.setSelected(!selectAll.isSelected());
                uidFragment = mAdapter.getCurrentFragment();
                uidFragment.mAdapter.selectAll(selectAll.isSelected());
                break;
        }
    }
}
