package com.example.mario.techinicianscheduler.Manager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by mario on 22/03/2017.
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
