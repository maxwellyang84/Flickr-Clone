package com.mendozae.teamflickr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Faves.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Faves#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Faves extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> likes;
    private FirebaseFirestore mStore;
    private DocumentReference userRef;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_faves, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);

        likes = new ArrayList<String>();




        mStore = FirebaseFirestore.getInstance();
        userRef = mStore.collection("Users").document(UserProfile.currentUser);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
             if(task.isSuccessful()){
                 DocumentSnapshot snapshot = task.getResult();
                 if (snapshot.exists()) {
                      likes = (ArrayList<String>) snapshot.get("Likes");
                      Collections.reverse(likes);
                     if (likes != null){
                         // use this setting to improve performance if you know that changes
                         // in content do not change the layout size of the RecyclerView
                         mRecyclerView.setHasFixedSize(true);


                         // use a linear layout manager
                         mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                         mRecyclerView.setLayoutManager(mLayoutManager);

                         // specify an adapter (see also next example)
                         mAdapter = new MyAdapter(likes);

                         mRecyclerView.setAdapter(mAdapter);

                         userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                             @Override
                             public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                Log.i("here", "faves");
                                 if(e!=null){
                                     return;
                                 }

                                 if(documentSnapshot !=null && documentSnapshot.exists()){
                                     Log.i("Here", "Faves");
                                     likes.clear();
                                     likes.addAll((ArrayList<String>) documentSnapshot.get("Likes"));
                                     Collections.reverse(likes);
                                     mAdapter.notifyDataSetChanged();


                                 }else{
                                     Log.i("info", "it's null");
                                 }
                             }
                         });
                     }
                 }
             }
            }
        });



    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.myViewHolder>{
        ArrayList<String> likes;

        public MyAdapter(ArrayList<String> likes){
            this.likes = likes;
        }
        @NonNull
        @Override
        public MyAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_custom_photos, viewGroup, false);
            MyAdapter.myViewHolder vh = new MyAdapter.myViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.myViewHolder myViewHolder, final int i) {

            myViewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), FullscreenImage.class);
                    intent.putExtra("Image", likes.get(i));
                    startActivity(intent);
                }
            });

            Glide.with(getContext()).load(likes.get(i)).into(myViewHolder.image);


        }

        @Override
        public int getItemCount() {
            return likes.size();
        }

        public class myViewHolder extends RecyclerView.ViewHolder{
            ImageView image;

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
