package com.mendozae.teamflickr;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import static com.mendozae.teamflickr.UserProfile.userReference;
import java.util.ArrayList;

public class Followers extends AppCompatActivity {


    ArrayList<String> followerNames;
    String[] DESCRIPTIONS;
    int[] IMAGES;
    SwipeRefreshLayout swipeRefresh;
    CustomAdapter adapter;
    ListView listView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        toolbar = (Toolbar) findViewById(R.id.followertoolbar);
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
                        followerNames = (ArrayList<String>) snapshot.get("Followers");
                        listView = (ListView) findViewById(R.id.followerlistview);
                        adapter = new CustomAdapter();
                        listView.setAdapter(adapter);

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


        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
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
            view = getLayoutInflater().inflate(R.layout.layout_custom, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.pfp);
            TextView textView_name = (TextView) view.findViewById(R.id.name);
            TextView textView_desc = (TextView) view.findViewById(R.id.desc);
//            imageView.setImageResource(IMAGES[i]);
            textView_name.setText(followerNames.get(i));
//            textView_desc.setText(DESCRIPTIONS[i]);
            return view;
        }
    }
}
