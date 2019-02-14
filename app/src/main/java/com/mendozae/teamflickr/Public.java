package com.mendozae.teamflickr;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

import javax.annotation.Nullable;


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
    private ArrayList<String> URLs;
    private FirebaseFirestore mStore;
    private CollectionReference photoRef;

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

        URLs = new ArrayList<String>();

        mStore = FirebaseFirestore.getInstance();
        photoRef = mStore.collection("Photos");
        final Query query = photoRef.whereEqualTo("User", UserProfile.currentUser );
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                    URLs.add((String) snapshot.get("URI"));
                }
                Collections.reverse(URLs);
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                mRecyclerView.setHasFixedSize(true);

                // use a linear layout manager
                mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(mLayoutManager);

                // specify an adapter (see also next example)
                mAdapter = new MyAdapter(URLs);
                mRecyclerView.setAdapter(mAdapter);
                query.addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        URLs.clear();
                        for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                            URLs.add((String) snapshot.get("URI"));
                        }
                        Collections.reverse(URLs);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("info", "it failed");
            }
        });


    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.myViewHolder>{
        List<String> imageURLs;

        public MyAdapter(List<String> imageURLs){
            this.imageURLs = imageURLs;
        }
        @NonNull
        @Override
        public MyAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_custom_photos, viewGroup, false);
            myViewHolder vh = new myViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.myViewHolder myViewHolder, final int i) {

            myViewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), FullscreenImage.class);
                    intent.putExtra("Image", imageURLs.get(i));
                    startActivity(intent);
                }
            });
           Glide.with(getContext()).load(imageURLs.get(i)).into(myViewHolder.image);
//                myViewHolder.image.setImageResource(images.get(i));

        }

        @Override
        public int getItemCount() {
            return imageURLs.size();
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
                image = (ImageView) view.findViewById(R.id.image);

            }
        }


    }
}
