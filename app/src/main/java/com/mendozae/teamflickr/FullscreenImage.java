package com.mendozae.teamflickr;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FullscreenImage extends AppCompatActivity {

    private ImageView image;
    private Button likeButton;
    private Button infoButton;
    private Button exitButton;
    private int state = 0;
    private FirebaseFirestore mStore;
    private DocumentReference userRef;
    private ArrayList<String> likedURLs;
    private String URL;
    private CollectionReference photoRef;
    private DocumentReference docRef;






    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
       final int tabIconColor = ContextCompat.getColor(FullscreenImage.this, R.color.tabUnselectedIconColor);
       final int iconColor = ContextCompat.getColor(FullscreenImage.this, R.color.starSelectedColor);

        Intent intent = getIntent();
        URL = intent.getStringExtra("Image");

        image = (ImageView) findViewById(R.id.imageView);
        Glide.with(FullscreenImage.this).load(URL).into(image);

        mStore= FirebaseFirestore.getInstance();

        photoRef = mStore.collection("Photos");

        Query query = photoRef.whereEqualTo("URI", URL);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(final QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                    docRef = snapshot.getReference();
                    infoButton = (Button) findViewById(R.id.infobutton);
                    infoButton.getBackground().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    infoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                            Intent intent;
                                            if(UserProfile.user.equals((String) snapshot.get("User"))) {
                                                intent = new Intent(FullscreenImage.this, PhotoInfo.class);
                                            }else{
                                                intent = new Intent(FullscreenImage.this, OtherPhotoInfo.class);
                                            }
                                            intent.putExtra("Image", URL);
                                            startActivity(intent);
                        }
                    });
                }

            }
        });

        mStore = FirebaseFirestore.getInstance();
        userRef = mStore.collection("Users").document(UserProfile.user);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){

                        likeButton = (Button) findViewById(R.id.likebutton);
                         likedURLs = (ArrayList<String>) snapshot.get("Likes");



                         if(likedURLs.contains(URL)){
                             likeButton.getBackground().setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
                             state = 1;
                         }else{
                             likeButton.getBackground().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                             state = 0;
                         }

                        exitButton = (Button) findViewById(R.id.exitbutton);
                        likeButton = (Button) findViewById(R.id.likebutton);

                        likeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(state == 0) {
                                    like();
                                }else{
                                    unlike();
                                }
                            }
                        });

                        exitButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onBackPressed();
                            }
                        });





                    }
                }
            }
        });






    }

    private void like(){
        int iconColor = ContextCompat.getColor(FullscreenImage.this, R.color.starSelectedColor);

        likeButton.getBackground().setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        state = 1;

        likedURLs.add(URL);
        userRef.update("Likes", FieldValue.arrayUnion(URL));
        docRef.update("Liked By", FieldValue.arrayUnion(UserProfile.user));
    }

    private void unlike(){
        int tabIconColor = ContextCompat.getColor(FullscreenImage.this, R.color.tabUnselectedIconColor);

        likeButton.getBackground().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        state = 0;
        likedURLs.remove(URL);
        userRef.update("Likes", FieldValue.arrayRemove(URL));

        docRef.update("Liked By", FieldValue.arrayRemove(UserProfile.user));

    }
}
