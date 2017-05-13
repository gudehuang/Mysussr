package com.example.hzg.mysussr.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hzg.mysussr.R;
import com.example.hzg.mysussr.application.MyApplication;
import com.example.hzg.mysussr.view.MyViewPager;
import com.example.hzg.mysussr.view.TabLinearLayout;

/**
 * Created by hzg on 2017/5/10.
 */

public class UidDialog extends DialogFragment {
    TabLinearLayout mTabLinrearLayout;
    MyViewPager mViewPager;
    Fragment[] fragments=new Fragment[3];
    FragmentManager mManager;

    public void setmManager(FragmentManager mManager) {
        this.mManager = mManager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_uid,container,false);
        mTabLinrearLayout= (TabLinearLayout) view.findViewById(R.id.fragment_uid_tablayout);
        mViewPager= (MyViewPager) view.findViewById(R.id.fragment_uid_viewpager);
        mTabLinrearLayout.setListener(new TabLinearLayout.TabonClickListener() {
            @Override
            public void onClick(int position) {
                mViewPager.setCurrentItem(position);
            }
        });
        mViewPager.setAdapter(new FragmentPagerAdapter(mManager) {
            @Override
            public Fragment getItem(int position) {
                if (position<fragments.length)
                {
                    if (fragments[position]==null)
                    {
                        fragments[position]=new UidFragment();
                    }
                    return fragments[position];
                }
                return  null;
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        return view;
    }

}
