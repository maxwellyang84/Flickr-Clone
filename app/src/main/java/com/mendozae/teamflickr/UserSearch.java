package com.mendozae.teamflickr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserSearch.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSearch extends Fragment {

    private FirebaseFirestore mStore;
    private CollectionReference userListRef;
    ListView listView;
    SwipeRefreshLayout swipeRefresh;
    CustomAdapter adapter;

    ArrayList<String> users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_search, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("hmm", "hmmm");
       if(adapter !=null){
           adapter.notifyDataSetChanged();
       }
        users = new ArrayList<>();
        mStore = FirebaseFirestore.getInstance();
        userListRef = mStore.collection("Users");
        userListRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                    Log.i("hello", "I am here 1");
                    String name = (String) snapshot.get("Name");
                    if(!name.equals(UserProfile.user)){
                        users.add(name);
                        Log.i("Hello", "adding");
                    }
                }
                 adapter = new CustomAdapter();
                listView = (ListView) getView().findViewById(R.id.listview);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getContext(), OtherUsersProfile.class);
                        intent.putExtra("Name", users.get(position));
                        startActivity(intent);
                    }
                });
                listView.setAdapter(adapter);

            }
        });



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
