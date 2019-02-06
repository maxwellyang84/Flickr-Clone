package com.mendozae.teamflickr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {

    ImageButton mImageButton;
    PopupMenu popup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mImageButton = (ImageButton) getView().findViewById(R.id.imageButton);
        popup = new PopupMenu(getContext(), mImageButton);
        popup.inflate(R.menu.menu_about);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
              popup.show();
            }
        });

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tablayout); //initializes the tablayout object
        for(int i = 0; i < 3; i++){
            tabLayout.addTab(tabLayout.newTab()); //adds three tabs
        }
        tabLayout.getTabAt(0).setText("Public"); //sets the name of tabs for each respective tab
        tabLayout.getTabAt(1).setText("Albums");
        tabLayout.getTabAt(2).setText("About");
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF")); //sets the tab indicator color

        final ViewPager viewPager = (ViewPager) getView().findViewById(R.id.page); //declares viewpager
        final UserProfilePagerAdapter adapter = new UserProfilePagerAdapter(getFragmentManager(),tabLayout.getTabCount());
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
                    logout();
                    return true;
            }
            return false;
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }


}
