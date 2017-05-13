package com.example.hzg.mysussr.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.hzg.mysussr.fragment.UidFragment;

import java.io.BufferedReader;

/**
 * Created by hzg on 2017/5/10.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
   private Fragment currentFragment;
    private Fragment[] fragments;
    public MyFragmentPagerAdapter(FragmentManager fm,Fragment[] fragments) {
        super(fm);
        this.fragments=fragments;
    }

    public <T extends Fragment> T getCurrentFragment() {
        return (T)currentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment= (Fragment) object;
        super.setPrimaryItem(container, position, object);
    }


    @Override
    public Fragment getItem(int position) {
        if (position < fragments.length) {
            if (fragments[position] == null) {
                switch (position)
                {
                    case 0:
                        fragments[position] = UidFragment.newUidFragment("TCPFX");
                        break;
                    case  1:
                        fragments[position] = UidFragment.newUidFragment("UDPFX");
                        break;
                    case 2:
                        fragments[position] = UidFragment.newUidFragment("UDPJW");
                        break;
                }

            }
            return fragments[position];
        }
        return null;
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
