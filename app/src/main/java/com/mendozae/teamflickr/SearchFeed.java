package com.mendozae.teamflickr;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class SearchFeed extends Fragment implements SearchView.OnQueryTextListener {

    public static SearchView searchView;
    SearchManager searchManager;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> images;



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
    public void onStart(){
        super.onStart();


        setupUI(getView().findViewById(R.id.framelayout));

        searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) getView().findViewById(R.id.searchview);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        searchView.setSearchableInfo( searchManager.getSearchableInfo(new
                ComponentName(getContext(),SearchableActivity.class)));
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();
                return false;
            }
        });

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        images = new ArrayList<>();
        mAdapter = new MyAdapter(images);

        mRecyclerView.setAdapter(mAdapter);

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
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<String> images;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageView mImageView;
            public MyViewHolder(View v) {
                super(v);

                mImageView = (ImageView) v.findViewById(R.id.image);

            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<String> images) {
            this.images = images;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_custom_photos, parent, false);

           MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), FullscreenImage.class);
                    intent.putExtra("image", R.drawable.elephant);
                    startActivity(intent);
                }
            });



        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return images.size();
        }
    }


}
