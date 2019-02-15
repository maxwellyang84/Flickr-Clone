package com.mendozae.teamflickr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import static com.mendozae.teamflickr.UserProfile.userReference;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link About.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link About#newInstance} factory method to
 * create an instance of this fragment.
 */
public class About extends Fragment {

    ArrayList<String> title;

    ArrayList<String> description;

    int[] image;

    TextView titles;
    TextView descriptions;
    ImageView images;



    @Override
    public void onCreate(Bundle SavedInstanceState) {

        super.onCreate(SavedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }



    @Override
    public void onStart(){
        super.onStart();
        final ListView listView = (ListView) getView().findViewById(R.id.aboutlistview);



        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        title = (ArrayList<String>)document.get("AboutKeys");
                        description = (ArrayList<String>) document.get("AboutValues");
                        image = new int[title.size()];
                        for(int i = 0; i < title.size(); i++){
                            image[i] = R.drawable.ic_arrow;
                        }
                        CustomAdapter adapter = new CustomAdapter();
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(getContext(), AboutEditActivity.class);
                                intent.putExtra("title", title.get(i));
                                intent.putExtra("description", description.get(i));
                                startActivity(intent);
                            }
                        });


                    }

                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {



    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return image.length;
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
            view = getLayoutInflater().inflate(R.layout.layout_custom_about, null);
            titles = (TextView) view.findViewById(R.id.userHolder);
            descriptions = (TextView) view.findViewById(R.id.description);
            images = (ImageView)view.findViewById(R.id.arrow);
            images.setImageResource(image[i]);
            titles.setText(title.get(i));

            descriptions.setText(description.get(i));

            return view;
        }
    }

}
