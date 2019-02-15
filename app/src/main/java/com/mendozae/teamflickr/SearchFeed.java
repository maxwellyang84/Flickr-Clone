package com.mendozae.teamflickr;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;


public class SearchFeed extends Fragment implements SearchView.OnQueryTextListener {

    public static SearchView searchView;
    SearchManager searchManager;
    private int tabPosition;
    public static SharedPreferences searchFeedSharedPreferences;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_feed, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();
        setupUI(getView().findViewById(R.id.framelayout));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tablayout); //initializes the tablayout object
        for(int i = 0; i < 2; i++){
            tabLayout.addTab(tabLayout.newTab()); //adds three tabs
        }
        tabLayout.getTabAt(0).setText("Photos"); //sets the name of tabs for each respective tab
        tabLayout.getTabAt(1).setText("Users");
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#F5F5F5")); //sets the tab indicator color

        final ViewPager viewPager = (ViewPager) getView().findViewById(R.id.page); //declares viewpager
        final SearchFeedAdapter adapter = new SearchFeedAdapter(getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        tabPosition = tabLayout.getSelectedTabPosition();
        searchFeedSharedPreferences = getContext().getSharedPreferences("com.mendozae.teamflickr", Context.MODE_PRIVATE);
        searchFeedSharedPreferences.edit().putInt("SearchTab", tabPosition).apply();


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { //sets an onTabSelected listener for when tabs are selected
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition()); //viewPager sets the tab position to the one that was selected
                Log.i("info", "Selected tab");
                tabPosition = tab.getPosition();
                searchFeedSharedPreferences.edit().putInt("SearchTab", tabPosition).apply();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupUI(getView().findViewById(R.id.framelayout));

        searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) getView().findViewById(R.id.searchview);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setBackgroundColor(Color.parseColor("#373738"));

            searchView.setSearchableInfo(searchManager.getSearchableInfo(new
                    ComponentName(getContext(), SearchableActivity.class)));

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();
                return false;
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();




    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // your search methods
        searchView.clearFocus();


        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }


    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }


    public static void hideSoftKeyboard(Activity activity) {
        if(activity!=null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            if(activity.getCurrentFocus() !=null) {
                inputMethodManager.hideSoftInputFromWindow(
                        activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }




}
