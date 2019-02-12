package com.mendozae.teamflickr;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class OtherUsersProfile extends AppCompatActivity {

    private Button button;
    private PopupMenu popup;
    private TextView name;
    private Toolbar toolbar;
    private String userProfileName;
    private String userCalledName;
    public static DocumentReference docRef;
    private FirebaseFirestore mStore;
    private TextView followers;
    private TextView following;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_users_profile);
        toolbar = (Toolbar) findViewById(R.id.backtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor("#333333"));

        Intent intent = getIntent();
        userProfileName = intent.getStringExtra("Name");
        UserProfile.currentUser = intent.getStringExtra("Name");
        userCalledName = UserProfile.user;


        mStore = FirebaseFirestore.getInstance();
        docRef = mStore.collection("Users").document(userProfileName);

        name = (TextView) findViewById(R.id.name);
        name.setText(userProfileName);
        button = (Button) findViewById(R.id.follow);
        popup = new PopupMenu(this, button);


        UserProfile.userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        ArrayList<String> following = (ArrayList<String>) snapshot.get("Following");
                        if(following.contains(userProfileName)){
                            button.setText("Followed");
                        }else{
                            button.setText("+  Follow");
                        }
                    }
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(button.getText().toString().equals("Followed")){
                    button.setText("+  Follow");
                    unfollow();
                }else{
                    button.setText("Followed");
                    follow();
                }
            }
        });




        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout); //initializes the tablayout object
        for(int i = 0; i < 3; i++){
            tabLayout.addTab(tabLayout.newTab()); //adds three tabs
        }
        tabLayout.getTabAt(0).setText("Public"); //sets the name of tabs for each respective tab
        tabLayout.getTabAt(1).setText("Faves");
        tabLayout.getTabAt(2).setText("About");
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#F5F5F5")); //sets the tab indicator color

        final ViewPager viewPager = (ViewPager) findViewById(R.id.page); //declares viewpager
        final OtherUserProfilePagerAdapter adapter = new OtherUserProfilePagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { //sets an onTabSelected listener for when tabs are selected
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition()); //viewPager sets the tab position to the one that was selected
                Log.i("info", "Selected tab");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        followers = (TextView) findViewById(R.id.followers);
                        following = (TextView)  findViewById(R.id.following);
                        Integer followersSize = ((ArrayList<String>) snapshot.get("Followers")).size();
                        Integer followingSize = ((ArrayList<String>) snapshot.get("Following")).size();
                        followers.setText(followersSize.toString() + " Followers");
                        following.setText(followingSize.toString() + " Following");
                        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if(e!=null){
                                    return;
                                }

                                if(documentSnapshot !=null && documentSnapshot.exists()){
                                    Integer followersSize = ((ArrayList<String>) documentSnapshot.get("Followers")).size();
                                    Integer followingSize = ((ArrayList<String>) documentSnapshot.get("Following")).size();
                                    followers.setText(followersSize.toString() + " Followers");
                                    following.setText(followingSize.toString() + " Following");


                                }else{
                                    Log.i("info", "it's null");
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void follow(){
        UserProfile.userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        ArrayList<String> following = (ArrayList<String>) snapshot.get("Following");
                        ArrayList<String> followingOrNot = (ArrayList<String>) snapshot.get("FollowingOrNotFollowing");
                        ArrayList<String> followers = (ArrayList<String>) snapshot.get("Followers");
                        ArrayList<String> followingOrNot2 = (ArrayList<String>) snapshot.get("FollowingOrNotFollowers");

                        if(followers.contains(userProfileName)){
                            followingOrNot2.set(followers.indexOf(userProfileName), "Followed");
                            UserProfile.userReference.update("FollowingOrNotFollowers", followingOrNot2);
                        }
                        following.add(userProfileName);
                        followingOrNot.add("Followed");

                        UserProfile.userReference.update("Following", following);
                        UserProfile.userReference.update("FollowingOrNotFollowing", followingOrNot);
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
                        ArrayList<String> followers = (ArrayList<String>) snapshot.get("Followers");
                        ArrayList<String> followingOrNot = (ArrayList<String>) snapshot.get("FollowingOrNotFollowers");
                        ArrayList<String> following = (ArrayList<String>) snapshot.get("Following");

                        followers.add(userCalledName);
                        if(following.contains(userCalledName)){
                            followingOrNot.add("Followed");
                        }else{
                            followingOrNot.add("+  Follow");
                        }

                        docRef.update("Followers", followers);
                        docRef.update("FollowingOrNotFollowers", followingOrNot);
                    }
                }
            }
        });
    }

    private void unfollow(){
        UserProfile.userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        ArrayList<String> following = (ArrayList<String>) snapshot.get("Following");
                        ArrayList<String> followingOrNot = (ArrayList<String>) snapshot.get("FollowingOrNotFollowing");
                        ArrayList<String> followers = (ArrayList<String>) snapshot.get("Followers");
                        ArrayList<String> followingOrNot2 = (ArrayList<String>) snapshot.get("FollowingOrNotFollowers");

                        followingOrNot.remove(following.indexOf(userProfileName));
                        following.remove(userProfileName);
                        if(followers.contains(userProfileName)){
                            followingOrNot2.set(followers.indexOf(userProfileName), "+  Follow");
                            UserProfile.userReference.update("FollowingOrNotFollowers", followingOrNot2);
                        }

                        UserProfile.userReference.update("Following", following);
                        UserProfile.userReference.update("FollowingOrNotFollowing", followingOrNot);

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
                        ArrayList<String> followers = (ArrayList<String>) snapshot.get("Followers");
                        ArrayList<String> followingOrNot = (ArrayList<String>) snapshot.get("FollowingOrNotFollowers");
                        ArrayList<String> following = (ArrayList<String>) snapshot.get("Following");
                        ArrayList<String> followingOrNot2 = (ArrayList<String>) snapshot.get("FollowingOrNotFollowing");


                        followingOrNot.remove(followers.indexOf(userCalledName));
                        followers.remove(userCalledName);


//                        if(following.contains(userCalledName)){
//                            followingOrNot2.remove(following.indexOf(userCalledName));
//                            docRef.update("FollowingOrNotFollowing", followingOrNot2);
//                        }

                        docRef.update("Followers", followers);
                        docRef.update("FollowingOrNotFollowers", followingOrNot);
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

    public void displayFollowers(View view){
        Intent intent = new Intent(OtherUsersProfile.this, OtherFollowers.class);
        intent.putExtra("Name", userProfileName);
        startActivity(intent);
    }

    public void displayFollowing(View view){
        Intent intent = new Intent(OtherUsersProfile.this, OtherFollowing.class);
        intent.putExtra("Name", userProfileName);
        startActivity(intent);
    }
}
