package com.mendozae.teamflickr;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;

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
                        followingNames = (ArrayList<String>) snapshot.get("Following");
                        listView = (ListView) findViewById(R.id.followinglistview);
                        adapter = new CustomAdapter();
                        listView.setAdapter(adapter);



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
                        followingNames = (ArrayList<String>) snapshot.get("Following");
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
        userReference.update("Following", FieldValue.arrayUnion(name));
    }

    private void unfollow(String name){
        userReference.update("Following", FieldValue.arrayRemove(name));
    }




    private class CustomAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

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
            if(view == null) {
                //view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_custom, viewGroup, false);
                view = getLayoutInflater().inflate(R.layout.layout_custom, null);
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.pfp);
            TextView textView_name = (TextView) view.findViewById(R.id.name);
            TextView textView_desc = (TextView) view.findViewById(R.id.desc);

//            view.findViewById(R.id.followbutton).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.i("Hello", "Please");
//                    ((ListView) viewGroup).performItemClick(view, i, 0);
//                }
//            });

            //imageView.setImageResource(IMAGES[i]);
            textView_name.setText(followingNames.get(index));
           // textView_desc.setText(DESCRIPTIONS[i]);
            return view;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i("fj;eafeaw",";ajf;aklwefjaw");
            long viewId = view.getId();
            if(viewId == R.id.followbutton){
                Log.i("YES", "PLEASE");

            }
        }




    }
}
