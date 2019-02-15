package com.mendozae.teamflickr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class MainActivity extends AppCompatActivity {
    //Landing Page
    private ImageView background, logo, backButton;
    private Button getStarted, logInOrSignUp;
    private EditText username, email, password;
    private TextView switcher;
    private Boolean signUp;
    public FirebaseFirestore ddb = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String userName;
    private String userEmail;
    private String userPassword;
    public static SharedPreferences userInterfaceSharedPreferences;
    Map<String, String> userInfo;


    public void enterMainFeed(View view) {

        userEmail = email.getText().toString();
        userPassword = password.getText().toString();
        userName = username.getText().toString();

        Log.i(userEmail, userPassword);
        if (signUp){
            /**do DB check, check for username/email conflict
             * if no conflict do password checking (optional)
             * signup user and move to main feed
             * */
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userName)
                                        .build();
                                mAuth.getCurrentUser().updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Log.i("Yes", "Successful");
                                                    initializeFields();
                                                    updateUI();
                                                }
                                            }
                                        });

                            } else {
                                if(password.getText().length() <6){
                                    Toast.makeText(MainActivity.this, "Password must contain at least 6 characters", Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(MainActivity.this, "Registration Failed, Please Try Again", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }
                    });

        } else {
            /**check DB for email/pass combination
             * login user and move to main feed
            * */
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Log in Successful", Toast.LENGTH_SHORT).show();
                        updateUI();
                    } else {

                        Toast.makeText(MainActivity.this, "Log in Unsuccessful", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }

    }
    /*
    initializes fields for the database for the user that just signed up
     */
    private void initializeFields(){
       List<String> title = Arrays.asList("Description", "Occupation", "Current city", "Hometown", "Website", "Tumblr", "Facebook", "Twitter", "Instagram",
                "Pinterest", "Email address");
      List<String> description =Arrays.asList("Add Description...", "Add Occupation...", "Add Current city...", "Add Hometown...",
                "Add Website...", "Add Tumblr...", "Add Facebook...", "Add Twitter...", "Add Instagram...", "Add Pinterest...",
                "Add Email address...");

        Map<String, List<String>> userInfo2 = new HashMap<>();


        userInfo.put("Email", userEmail);
        userInfo.put("Password", userPassword);
        userInfo.put("Name", userName);
        ddb.collection("Users").document(userName).set(userInfo);

        userInfo2.put("Followers", new ArrayList<String>());
        userInfo2.put("Following", new ArrayList<String>());
        userInfo2.put("Likes", new ArrayList<String>());
        userInfo2.put("Uploads", new ArrayList<String>());
        userInfo2.put("FollowingOrNotFollowing", new ArrayList<String>());
        userInfo2.put("FollowingOrNotFollowers", new ArrayList<String>());
        userInfo2.put("AboutKeys", title);
        userInfo2.put("AboutValues", description);
        userInfo2.put("Comments Posted", new ArrayList<String>());
        userInfo2.put("Previous Searches", new ArrayList<String>());
        ddb.collection("Users").document(userName).set(userInfo2, SetOptions.merge());
    }



    /*
    activates when the MainActivity class starts
     */
    @Override
    public void onStart() {

        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser(); //gets current user if any
        if(currentUser !=null){//checks if there is a current user

            updateUI(); //calls method to send Activity to the UserInterface Activity
        }
    }
/*
sends the UI to UserInterface class
 */
    private void updateUI(){
        Intent intent = new Intent(getApplicationContext(), UserInterface.class);
        userInterfaceSharedPreferences = this.getSharedPreferences("com.mendozae.teamflickr", Context.MODE_PRIVATE);
        userInterfaceSharedPreferences.edit().putString("username", mAuth.getCurrentUser().getDisplayName()).apply();
        userInterfaceSharedPreferences.edit().putInt("Tab", 0).apply();
        startActivity(intent);
    }
    public void switcher (View view){
        if (signUp){ //if currently on signup, then switch to login mode
            //switch to log in page
            username.setVisibility(View.GONE);
            signUp = false;
            logInOrSignUp.setText("Log in");
            switcher.setText(R.string.dontHave);

        } else {
            //switch to sign up page
            username.setVisibility(View.VISIBLE);
            username.setCursorVisible(true);
            signUp = true;
            logInOrSignUp.setText("Sign up");
            switcher.setText(R.string.alreadyHave);
        }
    }

    public void goBack(View view){
        username.animate().alpha(0f).setDuration(1000);
        email.animate().alpha(0f).setDuration(1000);
        password.animate().alpha(0f).setDuration(1000);
        logInOrSignUp.animate().alpha(0f).setDuration(1000);
        switcher.animate().alpha(0f).setDuration(1000);
        backButton.animate().alpha(0f).setDuration(1000);

        username.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        logInOrSignUp.setVisibility(View.GONE);
        switcher.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);

        background.animate().translationYBy(2000f).alpha(1f).setDuration(800);
        logo.animate().translationYBy(2000f).alpha(1f).setDuration(800);
        getStarted.animate().translationYBy(-1000f).alpha(1f).setDuration(800);


    }
    public void getStarted (View view){
        background.animate().translationYBy(-2000f).alpha(0f).setDuration(800);
        logo.animate().translationYBy(-2000f).alpha(0f).setDuration(800);
        getStarted.animate().translationYBy(1000f).alpha(0f).setDuration(800);

        if (signUp && username.getVisibility() == View.GONE) {
            username.setVisibility(View.VISIBLE);
        } else username.setVisibility(View.GONE);
        email.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        logInOrSignUp.setVisibility(View.VISIBLE);
        switcher.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);

        username.animate().alpha(1f).setDuration(1000);
        email.animate().alpha(1f).setDuration(1000);
        password.animate().alpha(1f).setDuration(1000);
        logInOrSignUp.animate().alpha(1f).setDuration(1000);
        switcher.animate().alpha(1f).setDuration(1000);
        backButton.animate().alpha(1f).setDuration(1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        signUp = true;

        userInfo = new HashMap<>();


        //set 1
        background = findViewById(R.id.splashBg);
        logo = findViewById(R.id.logo);
        getStarted = findViewById(R.id.getStarted);

        //set 2
        username = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        logInOrSignUp = findViewById(R.id.logInorSignUp);
        switcher = findViewById(R.id.switcher);
        backButton = findViewById(R.id.backButton);

        username.setVisibility(View.GONE);
        username.setAlpha(0f);
        email.setVisibility(View.GONE);
        email.setAlpha(0f);
        password.setVisibility(View.GONE);
        password.setAlpha(0f);
        logInOrSignUp.setVisibility(View.GONE);
        logInOrSignUp.setAlpha(0f);
        switcher.setVisibility(View.GONE);
        switcher.setAlpha(0f);
        backButton.setVisibility(View.GONE);
        backButton.setAlpha(0f);
    }
}
