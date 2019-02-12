package com.mendozae.teamflickr;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mendozae.teamflickr.UserProfile.userReference;
import static java.security.AccessController.getContext;

public class Following extends AppCompatActivity {


    String[] DESCRIPTIONS;
    int[] IMAGES;
    SwipeRefreshLayout swipeRefresh;
    CustomAdapter adapter;
    ListView listView;
    ArrayList<String> followingNames;
    Button followButton;
    ArrayList<String> followedOrNot;
    private FirebaseFirestore mStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        Toolbar toolbar = (Toolbar) findViewById(R.id.followingtoolbar);//initializes the toolbar
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
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        followingNames = (ArrayList<String>) snapshot.get("Following");
                        followedOrNot = (ArrayList<String>) snapshot.get("FollowingOrNotFollowing");
                        listView = (ListView) findViewById(R.id.followinglistview);
                        adapter = new CustomAdapter();
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String name = followingNames.get(i);
                                Intent intent = new Intent(Following.this, OtherUsersProfile.class);
                                intent.putExtra("Name", name);
                                intent.putExtra("State", "Following");
                                startActivity(intent);
                            }
                        });



                        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
                        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                Log.i("Info", "Refreshing");
                                updateFollowing();
                            }
                        });

                    }
                }
            }
        });
    }



    public void updateFollowing(){
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        followingNames = (ArrayList<String>) snapshot.get("Following");
                        followedOrNot = (ArrayList<String>) snapshot.get("FollowingOrNotFollowing");
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

    private void follow(String name){
        final DocumentReference docRef = mStore.collection("Users").document(name);
        userReference.update("Following", FieldValue.arrayUnion(name));
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        ArrayList<String> followingOrNotFollowingTemp = (ArrayList<String>) snapshot.get("FollowingOrNotFollowing");
                        followingOrNotFollowingTemp.add("Followed");
                        userReference.update("FollowingOrNotFollowing", followingOrNotFollowingTemp);
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
                        followingOrNotTemp.remove(followingNames.indexOf(name));
                        userReference.update("FollowingOrNotFollowing", followingOrNotTemp);
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




    private class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return followingNames.size();
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
        public View getView(final int i, View view, final ViewGroup viewGroup) {
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
            TextView textView_desc = (TextView) view.findViewById(R.id.desc);
            viewHolder.followButton.setText(followedOrNot.get(index));
            textView_name.setText(followingNames.get(index));

            viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(viewHolder.followButton.getText().toString().equals("Followed")){
                        viewHolder.followButton.setText("+  Follow");
                        unfollow(followingNames.get(index));

                    }else{
                        viewHolder.followButton.setText("Followed");
                        follow(followingNames.get(index));
                    }
                }
            });


            //imageView.setImageResource(IMAGES[i]);

           // textView_desc.setText(DESCRIPTIONS[i]);
            return view;
        }

        private class ViewHolder{
            Button followButton;
        }




    }
}
