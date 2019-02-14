package com.mendozae.teamflickr;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static com.mendozae.teamflickr.UserProfile.user;
import static com.mendozae.teamflickr.UserProfile.userReference;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class Followers extends AppCompatActivity {


    ArrayList<String> followerNames;
    ArrayList<String> followedOrNot;
    String[] DESCRIPTIONS;
    int[] IMAGES;
    public SwipeRefreshLayout swipeRefresh;
    public CustomAdapter adapter;
    ListView listView;
    Toolbar toolbar;
    private FirebaseFirestore mStore;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        toolbar = (Toolbar) findViewById(R.id.followertoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor("#333333"));

        mStore = FirebaseFirestore.getInstance();




    }

    @Override
    public void onStart(){
        super.onStart();
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    final DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        followerNames = (ArrayList<String>) snapshot.get("Followers");
                        followedOrNot = (ArrayList<String>) snapshot.get("FollowingOrNotFollowers");
                        listView = (ListView) findViewById(R.id.followerlistview);
                        adapter = new CustomAdapter();
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                final String name = followerNames.get(i);

                                Intent intent = new Intent(Followers.this, OtherUsersProfile.class);
                                intent.putExtra("Name", name);
                                intent.putExtra("State", "Followers");
                                startActivity(intent);
                            }
                        });



                        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
                        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                Log.i("Info", "Refreshing");
                                updateFollowers();
                            }
                        });

                    }
                }
            }
        });
    }



    public void updateFollowers(){
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        followerNames = (ArrayList<String>) snapshot.get("Followers");
                        followedOrNot = (ArrayList<String>) snapshot.get("FollowingOrNotFollowers");

                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    private void follow(final String name){
        final DocumentReference docRef = mStore.collection("Users").document(name);
        userReference.update("Following", FieldValue.arrayUnion(name));
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        ArrayList<String> followingOrNotTemp = (ArrayList<String>) snapshot.get("FollowingOrNotFollowing");
                        ArrayList<String> followingOrNotTemp2 = (ArrayList<String>) snapshot.get("FollowingOrNotFollowers");
                        ArrayList<String> followerNames = (ArrayList<String>) snapshot.get("Followers");

                        followingOrNotTemp.add("Followed");
                        followingOrNotTemp2.set(followerNames.indexOf(name), "Followed");

                        userReference.update("FollowingOrNotFollowing", followingOrNotTemp);
                        userReference.update("FollowingOrNotFollowers", followingOrNotTemp2);
                    }
                }
            }
        });

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                if(snapshot.exists()){
                    ArrayList<String> otherUsersFollowers = (ArrayList<String>) snapshot.get("Followers");
                    ArrayList<String> otherUsersFollowingOrNotTemp = (ArrayList<String>) snapshot.get("FollowingOrNotFollowers");
                    ArrayList<String> otherUsersFollowing = (ArrayList<String>) snapshot.get("Following");

                    otherUsersFollowers.add(UserProfile.user);

                    if(otherUsersFollowing.contains(UserProfile.user)){
                        otherUsersFollowingOrNotTemp.add("Followed");
                    }else{
                        otherUsersFollowingOrNotTemp.add("+  Follow");
                    }

                    docRef.update("Followers", otherUsersFollowers);
                    docRef.update("FollowingOrNotFollowers", otherUsersFollowingOrNotTemp);

                }
            }
        }
        });
    }

    private void unfollow(final String name){
        final DocumentReference docRef = mStore.collection("Users").document(name);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        ArrayList<String> followingOrNotTemp = (ArrayList<String>) snapshot.get("FollowingOrNotFollowing");
                        ArrayList<String> followingOrNotTemp2 = (ArrayList<String>) snapshot.get("FollowingOrNotFollowers");
                        ArrayList<String> followingNames = (ArrayList<String>) snapshot.get("Following");
                        ArrayList<String> followerNames = (ArrayList<String>) snapshot.get("Followers");

                        followingOrNotTemp.remove(followingNames.indexOf(name));
                        followingOrNotTemp2.set(followerNames.indexOf(name), "+  Follow");


                        userReference.update("FollowingOrNotFollowing", followingOrNotTemp);
                        userReference.update("FollowingOrNotFollowers", followingOrNotTemp2);
                        userReference.update("Following", FieldValue.arrayRemove(name));
                    }
                }
            }
        });

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        ArrayList<String> otherUsersFollowers = (ArrayList<String>) snapshot.get("Followers");
                        ArrayList<String> otherUsersFollowingOrNotTemp = (ArrayList<String>) snapshot.get("FollowingOrNotFollowers");

                        int index = otherUsersFollowers.indexOf(UserProfile.user);

                        otherUsersFollowers.remove(index);
                        otherUsersFollowingOrNotTemp.remove(index);

                        docRef.update("Followers", otherUsersFollowers);
                        docRef.update("FollowingOrNotFollowers", otherUsersFollowingOrNotTemp);

                    }
                }
            }
        });
    }

    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return followerNames.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final int index = i;
            final ViewHolder viewHolder;
            if(view == null) {
                view = getLayoutInflater().inflate(R.layout.layout_custom, null);
                viewHolder = new ViewHolder();
                viewHolder.followButton = (Button) view.findViewById(R.id.followbutton);
                view.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) view.getTag();
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.pfp);
            TextView textView_name = (TextView) view.findViewById(R.id.name);

            viewHolder.followButton.setText(followedOrNot.get(index));
            textView_name.setText(followerNames.get(index));

            viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(viewHolder.followButton.getText().toString().equals("Followed")){
                        viewHolder.followButton.setText("+  Follow");
                        unfollow(followerNames.get(index));
                    }else{
                        viewHolder.followButton.setText("Followed");
                        follow(followerNames.get(index));
                    }
                }
            });


            //imageView.setImageResource(IMAGES[i]);

            // textView_desc.setText(DESCRIPTIONS[i]);
            return view;
        }

        private class ViewHolder{
            public Button followButton;
        }
    }
}
