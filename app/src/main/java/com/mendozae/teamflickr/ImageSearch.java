package com.mendozae.teamflickr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private CollectionReference photoRef;
    private FirebaseFirestore mStore;
    private ArrayList<String> following;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_search, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        images = new ArrayList<>();
        mStore = FirebaseFirestore.getInstance();
        photoRef = mStore.collection("Photos");
        UserProfile.userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        Log.i("Hello", "doing following");
                         following = (ArrayList<String>) snapshot.get("Following");
                        photoRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(QueryDocumentSnapshot snapshot2: queryDocumentSnapshots){
                                    String currentUser = (String) snapshot2.get("User");
                                    if(!following.contains(currentUser) && !UserProfile.user.equals(currentUser)) {
                                        images.add((String) snapshot2.get("URI"));
                                        Log.i("hello", "adding photos");
                                    }
                                }
                                mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);
                                mRecyclerView.setHasFixedSize(true);
                                mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                                mRecyclerView.setLayoutManager(mLayoutManager);

                                mAdapter = new MyAdapter(images);
                                Log.i("hello", String.valueOf(images.size()));
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("hello","it failed");
                            }
                        });


                    }
                }
            }
        });


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
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            Glide.with(getContext()).load(images.get(position)).into(holder.mImageView);
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), FullscreenImage.class);
                    intent.putExtra("Image", images.get(position));
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
