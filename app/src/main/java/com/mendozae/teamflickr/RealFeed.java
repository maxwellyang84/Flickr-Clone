package com.mendozae.teamflickr;

import android.os.Build;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

public class RealFeed extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    TabItem tabChats;
    TabItem tabStatus;
    TabItem tabCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_feed);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        tabStatus = findViewById(R.id.tabStatus);
        tabCalls = findViewById(R.id.tabCalls);
        viewPager = findViewById(R.id.viewPager);
        tabChats = findViewById(R.id.tabChats);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
            if(tab.getPosition() == 1){
                toolbar.setBackgroundColor(ContextCompat.getColor(RealFeed.this, R.color.colorAccent));
                tabLayout.setBackgroundColor(ContextCompat.getColor(RealFeed.this, R.color.colorAccent));
                getWindow().setStatusBarColor(ContextCompat.getColor(RealFeed.this, R.color.colorAccent));
            }else if(tab.getPosition()==2){
                toolbar.setBackgroundColor(ContextCompat.getColor(RealFeed.this, android.R.color.darker_gray));
                tabLayout.setBackgroundColor(ContextCompat.getColor(RealFeed.this, android.R.color.darker_gray));
                getWindow().setStatusBarColor(ContextCompat.getColor(RealFeed.this, android.R.color.darker_gray));
            }else{
                toolbar.setBackgroundColor(ContextCompat.getColor(RealFeed.this, R.color.colorPrimary));
                tabLayout.setBackgroundColor(ContextCompat.getColor(RealFeed.this, R.color.colorPrimary));
                getWindow().setStatusBarColor(ContextCompat.getColor(RealFeed.this, R.color.colorPrimaryDark));
            }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}
