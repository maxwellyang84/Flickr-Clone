package com.mendozae.teamflickr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFeed .OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFeed #newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFeed extends Fragment {

    FirebaseFirestore mStore;
    FirebaseAuth mAuth;
    ArrayList<String> followingPhotos;

    @Override
    public void onStart(){
        super.onStart();
        final RecyclerView recyclerView = getView().findViewById(R.id.listView);
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //get which photos to show (get following list then get uploads list from each following)
        followingPhotos = new ArrayList<>();

        mStore.collection("Users").document(mAuth.getCurrentUser().getDisplayName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> following = (ArrayList<String>) documentSnapshot.get("Following");
                for (String user : following){
                    mStore.collection("Users").document(user).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ArrayList<String> uploads = (ArrayList<String>) documentSnapshot.get("Uploads");
                            for (String photoUId : uploads){
                                followingPhotos.add(photoUId);
                            }
                            Log.i("Number of Photos", Integer.toString(followingPhotos.size()));
                            CustomAdapter customAdapter = new CustomAdapter();
                            recyclerView.setAdapter(customAdapter);
                        }
                    });
                }

            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_feed, container, false);

    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
        @Override
        public int getCount() {
            return followingPhotos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custom_main_feed, null);

            final ImageView photo = convertView.findViewById(R.id.imageHolder),
            star = convertView.findViewById(R.id.star),
            info = convertView.findViewById(R.id.mainInfo);
            final TextView user = convertView.findViewById(R.id.userHolder),
            titleHolder = convertView.findViewById(R.id.title);

            mStore.collection("Photos").document(followingPhotos.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Glide.with(getContext()).load(documentSnapshot.get("URI")).into(photo);
                    user.setText(documentSnapshot.get("User").toString());
                    titleHolder.setText(documentSnapshot.get("Title").toString());

                }
            });

            return convertView;
        }
    }

}
