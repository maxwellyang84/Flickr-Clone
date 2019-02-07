package com.mendozae.teamflickr;

import android.graphics.Color;
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

public class Following extends AppCompatActivity {

    String[] NAMES;
    String[] DESCRIPTIONS;
    int[] IMAGES;
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        Toolbar toolbar = (Toolbar) findViewById(R.id.followingtoolbar);//initializes the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor("#333333"));

        ListView listView = (ListView) findViewById(R.id.followerlistview);
        CustomAdapter adapter = new CustomAdapter();
        //listView.setAdapter(adapter);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("Info", "Refreshing");
                updateFollowing();
            }
        });
    }

    private void updateFollowing(){


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
            return IMAGES.length;
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
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            TextView textView_name = (TextView) view.findViewById(R.id.name);
            TextView textView_desc = (TextView) view.findViewById(R.id.desc);
            imageView.setImageResource(IMAGES[i]);
            textView_name.setText(NAMES[i]);
            textView_desc.setText(DESCRIPTIONS[i]);
            return null;
        }
    }
}
