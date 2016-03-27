package com.lessask.dongfou;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

/**
 * Created by huangji on 2016/2/5.
 */
public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentDatas;
    private ArrayList<String> fragmentNames;
    public FragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        fragmentDatas = new ArrayList<>();
        fragmentNames = new ArrayList<>();
    }

    public void addFragment(Fragment fragment, String name){
        fragmentDatas.add(fragment);
        fragmentNames.add(name);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentDatas.get(position);
    }


    @Override
    public int getCount() {
        return fragmentDatas.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //return fragmentNames.get(position);
        return "";
    }
}

