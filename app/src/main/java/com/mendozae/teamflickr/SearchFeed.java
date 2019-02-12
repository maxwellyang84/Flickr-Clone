package com.mendozae.teamflickr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;


public class SearchFeed extends Fragment {

    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_search_feed, container, false);



    }

    @Override
    public void onStart(){
        super.onStart();
        searchView = (SearchView) getView().findViewById(R.id.searchview);
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
    }


}
