package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // The 3 Fragments of Session access ID UID and myFriend from this MainActivity
    static String ID; // to store the session
    static String UID; // UID stores the unique ID given to a user to uniquely identify it
    static String myFriend; // to store the name of the Friend whose tasks the user will be viewing

    static int frag_no; // to store on which session fragment should we navigate when we go to the Sessions

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frag_no = 1; // initially we will be at the first fragment which is to create or join a session

        // Intent to call for Authorization
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent i = new Intent(MainActivity.this,LoginRegisterActivity.class);
            startActivity(i);
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemHorizontalTranslationEnabled(true);
        // on app startup, we keep the Fragment Displaying Tasks on the Screen
        bottomNavigationView.setSelectedItemId(R.id.add_sign);

    }

    DisplayTasks_Fragment displayTasksFragment = new DisplayTasks_Fragment(); // this Fragment is to display our tasks and predict time
    Charts_Fragment chartsFragment = new Charts_Fragment(); // this Fragment is for Stats

    SessionCreate_Fragment createSessionFragment = new SessionCreate_Fragment(); // Enter into Session Details
    SessionDetails_Fragment sessionDetailsFragment = new SessionDetails_Fragment(); // List of Sessions
    SessionFriend_Fragment sessionFriendFragment = new SessionFriend_Fragment(); // List of Friends in my Session

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_sign:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, displayTasksFragment).commit();
                return true;

            case R.id.time_outline:
                switch (frag_no) {
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, createSessionFragment).commit();
                        return true;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, sessionDetailsFragment).commit();
                        return true;
                    case 3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, sessionFriendFragment).commit();
                        return true;
                }
                break;
            case R.id.pie_chart:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, chartsFragment).commit();
                return true;
        }
        return false;
    }

    void startLogin() {
        Intent i = new Intent(this, LoginRegisterActivity.class);
        startActivity(i);
        this.finish();
    }

    // this function is to SignOut and call for the Login Activity for the New User to Login
    public void handleLogout(View view) {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startLogin();
                }
            }
        });
    }
}