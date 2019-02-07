package com.mendozae.teamflickr;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

public class OtherUsersProfile extends AppCompatActivity {

    private ImageButton mImageButton;
    private PopupMenu popup;
    public static String user;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_users_profile);

        name = (TextView) findViewById(R.id.name);
        name.setText(user);
        mImageButton = (ImageButton) findViewById(R.id.imageButton);
        popup = new PopupMenu(this, mImageButton);
        popup.inflate(R.menu.menu_about);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
                popup.show();
            }
        });


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout); //initializes the tablayout object
        for(int i = 0; i < 3; i++){
            tabLayout.addTab(tabLayout.newTab()); //adds three tabs
        }
        tabLayout.getTabAt(0).setText("Public"); //sets the name of tabs for each respective tab
        tabLayout.getTabAt(1).setText("Albums");
        tabLayout.getTabAt(2).setText("About");
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#F5F5F5")); //sets the tab indicator color

        final ViewPager viewPager = (ViewPager) findViewById(R.id.page); //declares viewpager
        final OtherUserProfilePagerAdapter adapter = new OtherUserProfilePagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { //sets an onTabSelected listener for when tabs are selected
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition()); //viewPager sets the tab position to the one that was selected
                Log.i("info", "Selected tab");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener{
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch(menuItem.getItemId()){
                case R.id.logout:
                    return true;
            }
            return false;
        }
    }
}
