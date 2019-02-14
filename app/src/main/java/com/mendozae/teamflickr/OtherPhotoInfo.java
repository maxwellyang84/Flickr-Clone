package com.mendozae.teamflickr;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class OtherPhotoInfo extends AppCompatActivity {
    Toolbar toolbar;
    TextView title, description, location, user, tag, inputuser, inputtitle, inputdescription, inputlocation, inputtags, inputtimecreated;
    private FirebaseFirestore mStore;
    private CollectionReference photoRef;
    private String URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_photo_info);

        toolbar = (Toolbar) findViewById(R.id.infotoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor("#333333"));

        URL = getIntent().getStringExtra("Image");

        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        location = (TextView) findViewById(R.id.location);
        user = (TextView) findViewById(R.id.user);
        tag = (TextView) findViewById(R.id.tags);

        inputuser = (TextView) findViewById(R.id.inputuser);

        inputtitle = (TextView) findViewById(R.id.inputtitle);
        inputdescription = (TextView) findViewById(R.id.inputdescription);
        inputlocation = (TextView) findViewById(R.id.inputlocation);
        inputtags = (TextView) findViewById(R.id.inputtags);
        inputtimecreated = (TextView) findViewById(R.id.inputtimecreated);

        mStore = FirebaseFirestore.getInstance();
        photoRef = mStore.collection("Photos");
        Query query = photoRef.whereEqualTo("URI", URL);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                    String firebaseTitle = (String) snapshot.get("Title");
                    String firebaseDescription = (String) snapshot.get("Description");
                    String firebaseLocation = (String) snapshot.get("Location");
                    ArrayList<String> firebaseTags = (ArrayList<String>) snapshot.get("Tags");

                    String firebaseUser = (String) snapshot.get("User");
                    inputtitle.setText(firebaseTitle);
                    inputdescription.setText(firebaseDescription);
                    inputlocation.setText(firebaseLocation);
                    StringBuilder totalTags = new StringBuilder();
                    for(String s: firebaseTags){
                        totalTags.append(s + " ");
                    }
                    inputtags.setText(totalTags);
                    inputuser.setText(firebaseUser);

                    //input time created
                    Timestamp timestamp = (Timestamp) snapshot.get("Time Created");
                    LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestamp.getSeconds() , 0, ZoneOffset.UTC);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy - HH:mm a", Locale.ENGLISH);
                    String formattedDate = dateTime.format(formatter);
                    inputtimecreated.setText(formattedDate);


                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
}
