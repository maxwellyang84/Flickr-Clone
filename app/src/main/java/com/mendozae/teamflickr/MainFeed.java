package com.mendozae.teamflickr;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.DistributionOrBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


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
    CustomAdapter customAdapter;
    @Override
    public void onStart() {
        super.onStart();
        final RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //get which photos to show (get following list then get uploads list from each following)
        followingPhotos = new ArrayList<>();
        customAdapter = new CustomAdapter(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(customAdapter);


        mStore.collection("Users").document(mAuth.getCurrentUser().getDisplayName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> following = (ArrayList<String>) task.getResult().get("Following");
                for (String user : following) {
                    mStore.collection("Users").document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            ArrayList<String> uploads = (ArrayList<String>) task.getResult().get("Uploads");
                            if (!uploads.isEmpty()) {
                                for (String photoUId : uploads) {
                                    followingPhotos.add(photoUId);
                                }
                                customAdapter.notifyDataSetChanged();
                            }
                            Log.i("numPhotos", Integer.toString(followingPhotos.size()));
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
        private Context context;
        public CustomAdapter(Context context){
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_main_feed, null));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            final ViewHolder vHolder = viewHolder;
            final int index = i;
            mStore.collection("Photos").document(followingPhotos.get(index)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {

                    // set image
                    Glide.with(getContext()).load(documentSnapshot.get("URI")).into(vHolder.photo);
                    vHolder.photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent (getContext(), FullscreenImage.class);
                            intent.putExtra("Image", documentSnapshot.get("URI").toString());
                            startActivity(intent);
                        }
                    });
                    //set user and on click method
                    vHolder.user.setText(documentSnapshot.get("User").toString());
                    vHolder.user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO set intent to go back to mainfeed
                            Intent intent = new Intent(getContext(), OtherUsersProfile.class);
                            intent.putExtra("Name", documentSnapshot.get("User").toString());
                            startActivity(intent);
                        }
                    });

                    //set title
                    vHolder.title.setText(documentSnapshot.get("Title").toString());

                    //Set date
                    Timestamp timestamp = (Timestamp) documentSnapshot.get("Time Created");
                    LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestamp.getSeconds() , 0, ZoneOffset.UTC);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
                    String formattedDate = dateTime.format(formatter);
                    vHolder.time.setText(formattedDate);

                    //set star color (on defualt the star is set to "not like" mode)
                    /*if (init == 0){
                        init = 1;
                        ArrayList<String> likedBy = (ArrayList<String>) documentSnapshot.get("Liked By");
                        if (likedBy.contains(mAuth.getCurrentUser().getDisplayName())) {
                            vHolder.star.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.starSelectedColor), PorterDuff.Mode.SRC_IN);
                            state = 1;
                        } else {
                            vHolder.star.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.tabUnselectedIconColor), PorterDuff.Mode.SRC_IN);
                            state = 0;
                        }
                    } else {
                        if (state == 0){
                            vHolder.star.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.tabUnselectedIconColor), PorterDuff.Mode.SRC_IN);
                        } else {
                            vHolder.star.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.starSelectedColor), PorterDuff.Mode.SRC_IN);
                        }
                    }

                  // set star on click
                    vHolder.star.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //check if currentUser has liked the image or not
                           // ArrayList<String> likedBy = (ArrayList<String>) documentSnapshot.get("Liked By");
                            //if(likedBy.contains(mAuth.getCurrentUser().getDisplayName()))  {
                            if (state == 1){
//                                vHolder.star.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.tabUnselectedIconColor), PorterDuff.Mode.SRC_IN);
                                mStore.collection("Photos").document(followingPhotos.get(index)).update("Liked By", FieldValue.arrayRemove(mAuth.getCurrentUser().getDisplayName()));
                                mStore.collection("Users").document(mAuth.getCurrentUser().getDisplayName()).update("Likes", FieldValue.arrayRemove(documentSnapshot.get("URI")));
                                state = 0;
                            } else {
//                                vHolder.star.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.starSelectedColor), PorterDuff.Mode.SRC_IN);
                                //update photo fields (Liked By -- should changed like count) TODO take out Number of Likes (just do size of Liked By)
                                mStore.collection("Photos").document(followingPhotos.get(index)).update("Liked By", FieldValue.arrayUnion(mAuth.getCurrentUser().getDisplayName()));
                                //update user field
                                mStore.collection("Users").document(mAuth.getCurrentUser().getDisplayName()).update("Likes", FieldValue.arrayUnion(documentSnapshot.get("URI")));
                                state = 1;
                            }
                            notifyDataSetChanged();
                        }
                    });*/

                    //set num likes
                    vHolder.numLikes.setText(Integer.toString(((ArrayList<String>) documentSnapshot.get("Liked By")).size()));
                    //set info listener
                    vHolder.info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO set intent to go back to mainfeed
                            Intent intent = new Intent(getContext(), OtherPhotoInfo.class);
                            intent.putExtra("Image", documentSnapshot.get("URI").toString());
                            startActivity(intent);
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return followingPhotos.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView photo, info;
            private TextView user, title, time, numLikes;
            private Button star;
            public ViewHolder(View view){
                super(view);
                this.photo = view.findViewById(R.id.imageHolder);
                this.star = view.findViewById(R.id.star);
                this.info = view.findViewById(R.id.mainInfo);
                this.user = view.findViewById(R.id.userHolder);
                this.title = view.findViewById(R.id.title);
                this.time = view.findViewById(R.id.time);
                this.numLikes = view.findViewById(R.id.numLike);
            }

        }
    }

}
