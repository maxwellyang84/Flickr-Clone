package com.mendozae.teamflickr;

import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.mendozae.teamflickr.UserProfile.userReference;

public class Following extends AppCompatActivity {


    String[] DESCRIPTIONS;
    int[] IMAGES;
    SwipeRefreshLayout swipeRefresh;
    CustomAdapter adapter;
    ListView listView;
    Map<String,String> following;
    ArrayList<String> followingNames;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        Toolbar toolbar = (Toolbar) findViewById(R.id.followingtoolbar);//initializes the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor("#333333"));


        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        following = (LinkedHashMap<String,String>) snapshot.get("Following");
                        listView = (ListView) findViewById(R.id.followinglistview);
                        adapter = new CustomAdapter();
                        listView.setAdapter(adapter);
                        followingNames = (ArrayList<String>) following.keySet();



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



    private void updateFollowing(){
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        following = (LinkedHashMap<String,String>) snapshot.get("Following");
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
       userReference.update(name, "Followed");
    }

    private void unfollow(String name){
        userReference.update("Following", FieldValue.arrayRemove(name));
        Map<String, Object> deleteMap =new HashMap<>();
        deleteMap.put("Following." + name, FieldValue.delete());
        userReference.update(deleteMap);
    }




    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return following.size();
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
                //view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_custom, viewGroup, false);
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               view = getLayoutInflater().inflate(R.layout.layout_custom, null);
               viewHolder = new ViewHolder();
               view.setTag(viewHolder);
               viewHolder.followingButton = (Button) view.findViewById(R.id.followbutton);
            }else{
                viewHolder = (ViewHolder) view.getTag();
            }

            ImageView imageView = (ImageView) view.findViewById(R.id.pfp);
            TextView textView_name = (TextView) view.findViewById(R.id.name);
            TextView textView_desc = (TextView) view.findViewById(R.id.desc);
            viewHolder.followingButton.setText(following.get(followingNames.get(i)));

           viewHolder.followingButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    if(viewHolder.followingButton.getText().toString().equals("Followed")){
                        viewHolder.followingButton.setText("+  Follow");
                        unfollow(following.get(i));
                    }else{
                        viewHolder.followingButton.setText("Followed");
                        follow(following.get(i));
                    }
               }
           });



            //imageView.setImageResource(IMAGES[i]);
            textView_name.setText(following.get(index));
           // textView_desc.setText(DESCRIPTIONS[i]);
            return view;
        }

        private class ViewHolder{
            private Button followingButton;
        }





    }
}
