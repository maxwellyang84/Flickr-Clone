package com.mendozae.teamflickr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.mendozae.teamflickr.UserProfile.userReference;
import static com.mendozae.teamflickr.UserProfile.user;

public class AboutEditActivity extends AppCompatActivity {

    String[] editText;
    EditText text;
    Button button;
    InputMethodManager imm;
    String title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_edit);

        button = (Button) findViewById(R.id.edit);



        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");

        Toolbar toolbar = (Toolbar) findViewById(R.id.aboutedittoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor("#333333"));
        getSupportActionBar().setTitle(title);

        ListView listView = (ListView) findViewById(R.id.textlistview);
        editText = new String[1];
        editText[0] = description;

        imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);



        CustomAdapter adapter = new CustomAdapter();
        listView.setAdapter(adapter);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(button.getText().toString().equals("Done")){
                    final String textValue = text.getText().toString();
                    if(!textValue.equals("")) {
                        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot snapshot = task.getResult();
                                    if (snapshot.exists()) {
                                        ArrayList<String> titles = (ArrayList<String>) snapshot.get("AboutKeys");
                                        ArrayList<String> description = (ArrayList<String>) snapshot.get("AboutValues");
                                        description.set(titles.indexOf(title), textValue);
                                        userReference.update("AboutValues", description).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i("Success", "Succeeded");
                                                imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
                                                onBackPressed();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("Failure", "Failed");
                                            }
                                        });

                                    }
                                }

                            }
                        });
                    }else{
                        imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
                        onBackPressed();
                    }

            }else{
                    text.requestFocus();

                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    button.setText("Done");
                }
            }
        });





    }
    @Override
    public boolean onSupportNavigateUp(){
        imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
        onBackPressed();
        return true;
    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return editText.length;
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
            view = getLayoutInflater().inflate(R.layout.layout_about_edit_custom, null);
            text = (EditText) view.findViewById(R.id.editText);
            if(editText[i].equals("Add " + title +"...")){
                text.setHint(editText[i]);
            }else{
                text.setText(editText[i]);
            }

            text.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    if(MotionEvent.ACTION_UP == event.getAction()) {
                        button.setText("Done");
                    }
                    return false;
                }
            });
            return view;
        }
    }
    //implement if the edit text is empty, you will set the required hint
}
