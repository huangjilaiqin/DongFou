package com.lessask.dongfou;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.awt.font.TextAttribute;
import java.util.ArrayList;

/**
 * Created by huangji on 2016/2/5.
 */
public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentDatas;
    private ArrayList<String> fragmentNames;
    private ArrayList<SportGather> sportGathers;
    private FragmentManager fm;
    private String TAG = FragmentPagerAdapter.class.getSimpleName();
    public FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        fragmentDatas = new ArrayList<>();
        fragmentNames = new ArrayList<>();
        sportGathers = new ArrayList<>();
    }

    public void setSportGathers(ArrayList<SportGather> sportGathers) {
        this.sportGathers = sportGathers;
    }

    public void setFragments(ArrayList<Fragment> fragments) {
        if(this.fragmentDatas!= null){
            FragmentTransaction ft = fm.beginTransaction();
            for(Fragment f:this.fragmentDatas){
                ft.remove(f);
            }
            ft.commit();
            ft=null;
            fm.executePendingTransactions();
        }
        this.fragmentDatas = fragments;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.e(TAG, "instantiateItem");
        FragmentData fragmentData = (FragmentData) super.instantiateItem(container, position);
        //在这里传递fragment数据
        fragmentData.setSportGather(sportGathers.get(position));
        return fragmentData;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
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

