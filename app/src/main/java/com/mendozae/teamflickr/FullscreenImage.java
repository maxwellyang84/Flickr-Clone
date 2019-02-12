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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class FullscreenImage extends AppCompatActivity {

    private ImageView image;
    private Button likeButton;
    private Button infoButton;
    private Button exitButton;
    private int state = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        Intent intent = getIntent();
        String URL = intent.getStringExtra("Image");


        image = (ImageView) findViewById(R.id.imageView);
        Glide.with(FullscreenImage.this).load(URL).into(image);

        exitButton = (Button) findViewById(R.id.exitbutton);
        likeButton = (Button) findViewById(R.id.likebutton);
        infoButton = (Button) findViewById(R.id.infobutton);
        int tabIconColor = ContextCompat.getColor(FullscreenImage.this, R.color.tabUnselectedIconColor);
        likeButton.getBackground().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        infoButton.getBackground().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state == 0) {
                    int iconColor = ContextCompat.getColor(FullscreenImage.this, R.color.starSelectedColor);

                    likeButton.getBackground().setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
                    state = 1;
                }else{
                    int tabIconColor = ContextCompat.getColor(FullscreenImage.this, R.color.tabUnselectedIconColor);

                    likeButton.getBackground().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    state = 0;
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FullscreenImage.this, PhotoInfo.class);
                startActivity(intent);
            }
        });



    }
}
