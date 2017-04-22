package com.example.mario.techinicianscheduler.Manager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * A customized page adapter for the side Menu.
 * These code is from the library.
 */

public class MyPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;
    public MyPageAdapter(FragmentManager fm) {
        super(fm);
    }
    public MyPageAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }
    @Override
    public int getCount() {
        return list.size();
    }
}
