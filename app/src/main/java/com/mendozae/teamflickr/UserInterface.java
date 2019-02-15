package com.mendozae.teamflickr;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.Objects;

import static com.mendozae.teamflickr.SearchFeed.searchFeedSharedPreferences;
import static com.mendozae.teamflickr.SearchFeed.searchView;

public class UserInterface extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private  ViewPager viewPager;
    private TabLayout tabLayout;
    private InputMethodManager imm;
    private int tabIconColor2, tabIconColor, tabPosition;
    /**
     * The {@link ViewPager} that will host the section contents.
     */


    @Override
    public void onStart(){
        super.onStart();

        tabPosition = MainActivity.userInterfaceSharedPreferences.getInt("Tab", 2);

        viewPager.setCurrentItem(tabPosition);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_interface);

        imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);

         tabIconColor2 = ContextCompat.getColor(UserInterface.this, R.color.tabUnselectedIconColor);
        tabIconColor = ContextCompat.getColor(UserInterface.this, R.color.tabSelectedIconColor);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout); //initializes the tablayout object
        for(int i = 0; i <=3; i++) {
            tabLayout.addTab(tabLayout.newTab()); //adds four tabs to the tablayout
        }



        //sets the tab icon for each tab
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_photo_library);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_account);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_camera);

        //sets the tab color for each tab
        for(int i = 0; i <=3; i++){
            if(i==0) { //if the tab's the first one, it will be selected by default on start up so the colour will be white
                int tabIconColor = ContextCompat.getColor(UserInterface.this, R.color.tabSelectedIconColor);
                (tabLayout.getTabAt(i).getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }else{ //everything else will be great
                int tabIconColor = ContextCompat.getColor(UserInterface.this, R.color.tabUnselectedIconColor);
                (tabLayout.getTabAt(i).getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
         viewPager = (ViewPager) findViewById(R.id.pager); //declares viewPager
        final PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount()); //sets the adapter with the
        //support FragmentManager, and the number of tabs
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){ //a listener for when tabs are selected

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                //when a tab is selected the viewPager moves to that tab location
                //changes color of tab to highlighted
                (tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

               // imm.hideSoftInputFromWindow(SearchView.getWindowToken(), 0);
                if(searchView !=null){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
                Log.i("fjaf", String.valueOf(tab.getPosition()));
                MainActivity.userInterfaceSharedPreferences.edit().putInt("Tab", tab.getPosition()).apply();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //changes color of tab to unhighlighted
                (tab.getIcon()).setColorFilter(tabIconColor2, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayFollowers(View view){
       Intent intent = new Intent(UserInterface.this, Followers.class);
       startActivity(intent);
    }

    public void displayFollowing(View view){
        Intent intent = new Intent(UserInterface.this, Following.class);
        startActivity(intent);
    }

}
