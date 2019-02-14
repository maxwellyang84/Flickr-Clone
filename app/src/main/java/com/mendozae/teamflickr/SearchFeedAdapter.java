package com.mendozae.teamflickr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class SearchFeedAdapter extends FragmentPagerAdapter {

    int mNumOfTabs;

    public SearchFeedAdapter(FragmentManager fm, int numTabs){
        super(fm);
        this.mNumOfTabs = numTabs;
    }
    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                Log.i("1", "1");
                return new ImageSearch(); //if the first tab is pressed, it creates a new Public() fragment object
            case 1:
                Log.i("2", "2");
                return new UserSearch(); //if the second tab is pressed, it creates a new Albums() fragment object
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    } //
}
