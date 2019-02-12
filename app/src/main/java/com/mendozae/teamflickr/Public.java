package com.mendozae.teamflickr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Public.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Public#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Public extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Integer> myDataset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_public, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);
        myDataset = new ArrayList<Integer>();
        myDataset.add(R.drawable.flickr);
        myDataset.add(R.drawable.flickrlogo);
        myDataset.add(R.drawable.backbutton);
        myDataset.add(R.drawable.dots);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.myViewHolder>{
        ArrayList<Integer> images;

        public MyAdapter(ArrayList<Integer> images){
            this.images = images;
        }
        @NonNull
        @Override
        public MyAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_custom_photos, viewGroup, false);
            myViewHolder vh = new myViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.myViewHolder myViewHolder, int i) {

                myViewHolder.image.setImageResource(images.get(i));

        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        public class myViewHolder extends RecyclerView.ViewHolder{
            ImageView image, image2;

            public myViewHolder(@NonNull View view) {
                super(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("Hello", "HEllo");
                    }
                });
                image = (ImageView) view.findViewById(R.id.imageView);

            }
        }
    }
}
