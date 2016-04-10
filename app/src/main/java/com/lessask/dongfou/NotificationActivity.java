package com.lessask.dongfou;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity implements FragmentFeedback.OnFragmentInteractionListener,FragmentNotices.OnFragmentInteractionListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("通知中心");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        int versionCode = getIntent().getIntExtra("versionCode", 0);

        MyFragmentDataPagerAdapter myFragmentPagerAdapter = new MyFragmentDataPagerAdapter(getSupportFragmentManager());
        myFragmentPagerAdapter.addFragment(FragmentNotices.newInstance(this,versionCode), "系统通知");
        myFragmentPagerAdapter.addFragment(new FragmentFeedback(), "反馈通知");
        viewPager.setAdapter(myFragmentPagerAdapter);

        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class MyFragmentDataPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentDatas;
        private ArrayList<String> fragmentNames;
        public MyFragmentDataPagerAdapter(FragmentManager fm) {
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
            return fragmentNames.get(position);
        }
    }
}
