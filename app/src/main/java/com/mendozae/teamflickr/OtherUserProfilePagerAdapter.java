package com.mendozae.teamflickr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class OtherUserProfilePagerAdapter extends FragmentPagerAdapter{
    int mNumOfTabs;
    public OtherUserProfilePagerAdapter(FragmentManager fm, int numTabs){
        super(fm);
        this.mNumOfTabs = numTabs;
    }
    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new Public(); //if the first tab is pressed, it creates a new Public() fragment object
            case 1:
                return new Albums(); //if the second tab is pressed, it creates a new Albums() fragment object
            case 2:
                return new OtherAbout(); //if the third tab is pressed, it creates a new About() fragment object
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    } //returns how many tabs there are intended to be
}
