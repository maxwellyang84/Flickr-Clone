package com.mendozae.teamflickr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    private int numOfTabs;

    PageAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position){
            switch(position){
                case 0:
                    return new MainFeed();
                case 1:
                    return new SearchFeed();
                case 2:
                    return new UserProfile();
                case 3:
                    return new MyCamera();
                default:
                    return null;
            }
    }

    @Override
    public int getCount(){return numOfTabs;}
}
