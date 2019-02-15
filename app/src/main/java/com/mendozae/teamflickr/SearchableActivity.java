package com.mendozae.teamflickr;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {

    private FirebaseFirestore mStore;
    private CollectionReference photoRef;
    private List<String> images;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;
    private ListView listView;
    private CustomAdapter customAdapter;
    private ArrayList<String> users;
    private CollectionReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);


        toolbar = (Toolbar) findViewById(R.id.backtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor("#333333"));
        getSupportActionBar().setTitle("Search");


        mStore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        listView = (ListView) findViewById(R.id.listview);


        int tab = SearchFeed.searchFeedSharedPreferences.getInt("SearchTab", 2);

        if(tab == 0) {
            listView.setVisibility(View.GONE);
            photoRef = mStore.collection("Photos");
            images = new ArrayList<>();


            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new MyAdapter(images);
            mRecyclerView.setAdapter(mAdapter);



            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);

                doMyImageSearch(Arrays.asList(query.split(" ")));
            }
        }else{
            mRecyclerView.setVisibility(View.GONE);
            customAdapter = new CustomAdapter();
            userRef = mStore.collection("Users");
            users = new ArrayList<>();
            listView.setAdapter(customAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent;
                    if(!users.get(position).equals(UserProfile.user)) {
                        intent = new Intent(SearchableActivity.this, OtherUsersProfile.class);
                    }else{
                        intent = new Intent(SearchableActivity.this, UserInterface.class);
                        MainActivity.userInterfaceSharedPreferences.edit().putInt("Tab", 2).apply();
                    }
                    intent.putExtra("Name", users.get(position));
                    startActivity(intent);
                }
            });


            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);

                doMyUserSearch(query);
            }
        }

    }



    private void doMyImageSearch(List<String> query){
        for(int i = 0; i < query.size(); i++) {
           Query mainQuery = photoRef.whereArrayContains("Tags", query.get(i));
           mainQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
               @Override
               public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                   Log.i("I am here", "1");
                    for(QueryDocumentSnapshot s: queryDocumentSnapshots){
                        String url = (String) s.get("URI");
                        Log.i("I am here", "2");
                        if(!images.contains(url)) {
                            images.add(url);
                            Log.i("I am here", "3");
                        }
                    }
                   mAdapter.notifyDataSetChanged();

               }
           });
        }

    }

    private void doMyUserSearch(String query){
        Query mainQuery = userRef.whereEqualTo("Name", query);
        mainQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot s: queryDocumentSnapshots){
                    String name = (String) s.get("Name");
                    users.add(name);
                }
                customAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)   {
// something here
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<String> images;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageView mImageView;
            public MyViewHolder(View v) {
                super(v);

                mImageView = (ImageView) v.findViewById(R.id.image);

            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<String> images) {
           this.images = images;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            View v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_custom_photos, parent, false);

            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Log.i("sup", String.valueOf(position));
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchableActivity.this, FullscreenImage.class);
                    intent.putExtra("Image", images.get(position));
                    startActivity(intent);
                }
            });
            Log.i("feawfae", String.valueOf(images.size()));
            Glide.with(SearchableActivity.this).load(images.get(position)).into(holder.mImageView);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            Log.i("feawfae", String.valueOf(images.size()));
            return images.size();
        }
    }


    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return users.size();
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
            viewHolder.followButton.setVisibility(View.GONE);

            textView_name.setText(users.get(index));




            //imageView.setImageResource(IMAGES[i]);

            // textView_desc.setText(DESCRIPTIONS[i]);
            return view;
        }

        private class ViewHolder{
            public Button followButton;
        }
    }
}
