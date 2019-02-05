package com.mendozae.teamflickr;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class UserProfilePagerAdapter extends FragmentPagerAdapter {

    int mNumOfTabs;
    public UserProfilePagerAdapter(FragmentManager fm, int numTabs){
        super(fm);
        this.mNumOfTabs = numTabs;
    }
    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new Public();
            case 1:
                return new Albums();
            case 2:
                return new About();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
