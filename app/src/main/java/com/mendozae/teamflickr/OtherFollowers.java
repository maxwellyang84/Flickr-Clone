package com.mendozae.teamflickr;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class OtherFollowers extends AppCompatActivity {


    Toolbar toolbar;
    Intent intent;
    String userShown;
    private FirebaseFirestore mStore;
    private DocumentReference userShownRef;
    ListView listView;
    SwipeRefreshLayout swipeRefresh;
    ArrayList<String> followers;
    ArrayList<String> followingOrNot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_followers);
        toolbar = (Toolbar) findViewById(R.id.followertoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor("#333333"));

        intent = getIntent();
        userShown = intent.getStringExtra("Name");

        mStore = FirebaseFirestore.getInstance();
        userShownRef = mStore.collection("Users").document(userShown);

        userShownRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              if(task.isSuccessful()){
                  DocumentSnapshot snapshot = task.getResult();
                  if(snapshot.exists()){
                      followers = (ArrayList<String>) snapshot.get("Followers");
                      followingOrNot = (ArrayList<String>) snapshot.get("FollowingOrNotFollowers");

                      listView = (ListView) findViewById(R.id.followerlistview);
                      CustomAdapter adapter = new CustomAdapter();
                      listView.setAdapter(adapter);

                      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                          @Override
                          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                              final String name = followers.get(i);
                              Intent intent;
                              if(name.equals(UserProfile.user)){
                                  intent = new Intent(OtherFollowers.this, UserInterface.class);
                                  TabLayout.Tab tab = UserInterface.tabLayout.getTabAt(2);
                                  tab.select();
                              }else {
                                  intent = new Intent(OtherFollowers.this, OtherUsersProfile.class);
                                  intent.putExtra("Name", name);
                              }
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

    private void updateFollowers(){

    }

    private void follow(final String name){

    }

    private void unfollow(final String name){

    }


    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return followers.size();
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
            TextView textView_desc = (TextView) view.findViewById(R.id.desc);
            viewHolder.followButton.setText(followingOrNot.get(index));
            textView_name.setText(followers.get(index));

            viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(viewHolder.followButton.getText().toString().equals("Followed")){
                        viewHolder.followButton.setText("+  Follow");
                        unfollow(followers.get(index));
                    }else{
                        viewHolder.followButton.setText("Followed");
                        follow(followers.get(index));
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
