package com.mendozae.teamflickr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ShareImage extends AppCompatActivity {

    EditText[] about = new EditText[4];
    ImageView thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_image);
        thumbnail = findViewById(R.id.thumbnail);
        about = new EditText[4];
        about[0] = findViewById(R.id.userHolder);
        about[1] = findViewById(R.id.tags);
        about[2] = findViewById(R.id.location);
        about[3] = findViewById(R.id.description);

        Intent intent = getIntent();
        String pathName = intent.getStringExtra("filename");

        final File f = new File(pathName, "profile.jpg");
        try {
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            thumbnail.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Button share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFB(f);
            }
        });
    }

    ProgressDialog progressDialog;
    int uid;

    private void saveToFB(final File f) {
        //upload to firebase storage
        //link to user (user should have a list of all images posted)
        //create a collection to hold about stuff
        MainActivity.userInterfaceSharedPreferences.edit().putInt("Tab", 2).apply();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Image...");
        progressDialog.show();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        final Map<String, String> photoInfo = new HashMap<>();
        final Map<String, List<String>> photoTags = new HashMap<>();

        photoInfo.put("User", auth.getCurrentUser().getDisplayName());
        photoInfo.put("Title", about[0].getText().toString());
        photoInfo.put("Location", about[2].getText().toString());
        photoInfo.put("Description", about[3].getText().toString());
        photoInfo.put("Number of Likes", Integer.toString(0));

        String tags[] = about[1].getText().toString().split(" ");
        photoTags.put("Tags", Arrays.asList(tags));
        photoTags.put("Liked By", new ArrayList<String>());

        //add photo document
        final FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        mStore.collection("Photos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                uid = queryDocumentSnapshots.size();
                final StorageReference imageRef = storageReference.child("img/" + auth.getCurrentUser().getUid()  + "/" + uid + ".jpg");
                try {
                    final UploadTask uploadTask = imageRef.putStream(new FileInputStream(f));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.hide();
                            //grab URI
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    photoInfo.put("URI", uri.toString());

                                    //add Timestamp to photo field
                                    final Map<String, FieldValue> photoTime = new HashMap<>();
                                    photoTime.put("Time Created", FieldValue.serverTimestamp());

                                    //add photo information
                                    mStore.collection("Photos").document(Integer.toString(uid)).set(photoInfo, SetOptions.merge());
                                    mStore.collection("Photos").document(Integer.toString(uid)).set(photoTags, SetOptions.merge());
                                    mStore.collection("Photos").document(Integer.toString(uid)).set(photoTime, SetOptions.merge());

                                    //link photo to user
                                    mStore.collection("Users").document(auth.getCurrentUser().getDisplayName()).update("Uploads", FieldValue.arrayUnion(Integer.toString(uid)));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ShareImage.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Intent intent = new Intent(ShareImage.this, UserInterface.class);
                            startActivity(intent);
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });



    }
}
