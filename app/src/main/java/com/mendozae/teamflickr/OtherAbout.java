package com.mendozae.teamflickr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import static com.mendozae.teamflickr.OtherUsersProfile.docRef;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OtherAbout.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OtherAbout#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherAbout extends Fragment {


    ArrayList<String> titles;
    ArrayList<String> descriptions;
    TextView title;
    TextView description;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_about, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {



        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        ArrayList<String> AboutKeys = (ArrayList<String>) snapshot.get("AboutKeys");
                        ArrayList<String> AboutValues = (ArrayList<String>) snapshot.get("AboutValues");
                        listView = (ListView) view.findViewById(R.id.otheraboutlistview);



                        titles = new ArrayList<>();
                        descriptions = new ArrayList<>();
                        for(int i = 0; i < AboutKeys.size(); i++){
                            if(!AboutValues.get(i).equals("Add " + AboutKeys.get(i) + "...")){
                                titles.add(AboutKeys.get(i));
                                descriptions.add(AboutValues.get(i));
                            }
                        }
                        CustomAdapter adapter = new CustomAdapter();
                        listView.setAdapter(adapter);
                    }
                }
            }
        });


    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return titles.size();
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
            view = getLayoutInflater().inflate(R.layout.layout_custom_about_other, null);
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            title.setText(titles.get(i));
            description.setText(descriptions.get(i));

            return view;
        }


    }


}
