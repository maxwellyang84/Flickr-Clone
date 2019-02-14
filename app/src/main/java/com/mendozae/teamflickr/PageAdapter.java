package com.mendozae.teamflickr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

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
                    Log.i("hello", "here");
                    return new MainFeed(); //if the first tab is pressed, creates a new MainFeed() fragment object
                case 1:
                    return new SearchFeed(); //if the second tab is pressed, creates a new SearchFeed() fragment object
                case 2:
                    return new UserProfile(); //if the third tab is pressed, creates a new UserProfile() fragment object
                case 3:
                    return new MyCamera(); //if the fourth tab is pressed, creates a new Camera() fragment object
                default:
                    return null;
            }
    }

    @Override
    public int getCount(){return numOfTabs;}
}
