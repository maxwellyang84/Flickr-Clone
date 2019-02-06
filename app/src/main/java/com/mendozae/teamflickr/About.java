package com.mendozae.teamflickr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link About.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link About#newInstance} factory method to
 * create an instance of this fragment.
 */
public class About extends Fragment {

    String[] title = {"Github","Description", "Occupation", "Current city", "Hometown", "Website", "Tumblr", "Facebook", "Twitter", "Instagram",
    "Pinterest", "Email address"};

    String[] description ={"Add Github...","Add Description...", "Add Occupation...", "Add Current city...", "Add Hometown...",
     "Add Website...", "Add Tumblr...", "Add Facebook...", "Add Twitter...", "Add Instagram...", "Add Pinterest...",
    "Add Email address..."};

    int[] image;

    TextView titles;
    TextView descriptions;
    ImageView images;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ListView listView = (ListView) getView().findViewById(R.id.aboutlistview);
        image = new int[title.length];
        for(int i = 0; i < title.length; i++){
            image[i] = R.drawable.ic_arrow;
        }



        CustomAdapter adapter = new CustomAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), AboutEditActivity.class);
                intent.putExtra("title", title[i]);
                intent.putExtra("description", description[i]);
                startActivity(intent);
            }
        });

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
            titles = (TextView) view.findViewById(R.id.title);
            descriptions = (TextView) view.findViewById(R.id.description);
            images = (ImageView)view.findViewById(R.id.arrow);
            images.setImageResource(image[i]);
            titles.setText(title[i]);
            descriptions.setText(description[i]);
            return view;
        }
    }

}
