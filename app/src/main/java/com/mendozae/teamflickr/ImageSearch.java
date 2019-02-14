package com.mendozae.teamflickr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageSearch.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageSearch extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> images;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_search, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        images = new ArrayList<>();
        mAdapter = new MyAdapter(images);

        mRecyclerView.setAdapter(mAdapter);
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
