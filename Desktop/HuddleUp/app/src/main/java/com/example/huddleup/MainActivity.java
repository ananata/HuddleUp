package com.example.huddleup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.huddleup.auth.and.database.Auth;

// these below imports probably can be removed from MainActivity
import com.example.huddleup.auth.and.database.Database;
import com.example.huddleup.fragments.LoginFragment;
import com.example.huddleup.fragments.RegisterFragment;

import java.util.ArrayList;

import OtherClasses.AuthenticationPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private Button getStartedButton;
    public static Context mainContext;

    // Declare auth variable
    //private FirebaseAuth mAuth;
    //private Auth mAuth;
    //private Database database;
    private String data;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        if (mainContext == null) {

            Auth.instance = new Auth();

            //mAuth = new Auth();
            //FirebaseUser currentUser = mAuth.getCurrentUser();
            // Checks if user is signed in
            if (Auth.instance.firebaseUser != null) {
                // Pass user information to HomeScreen
                Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                intent.putExtra("", "This message comes from passing data from Main " + "Activity");
                startActivity(intent);
            } else {
                Intent i = new Intent(MainActivity.this, this.getClass());
                startActivity(i);
                System.out.println("~!@~!@~!@~!@!~@~@~@~@~@~@~@~@~@~@");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // View splash screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = getBaseContext();

        // Display login and register fragments screen when button is clicked
        getStartedButton = (Button) findViewById(R.id.ButtonGetStarted);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonEffect(v);
                showLoginAndRegisterFragments();
            }
        });
    }

    /**
     * Lays Fragments on Login Activity Screen
     */
    public void showLoginAndRegisterFragments() {
        setContentView(R.layout.activity_login_screen);
        ViewPager viewPager = findViewById(R.id.viewPager);

        AuthenticationPagerAdapter pagerAdapter = new AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new LoginFragment());
        pagerAdapter.addFragment(new RegisterFragment());
        viewPager.setAdapter(pagerAdapter);
    }

    /**
     * Allows user to know when button has been clicked
     * Adapted from: https://stackoverflow.com/questions/7175873/how-to-set-button-click-effect-in-android
     *
     * @param button
     */
    private static void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // Button flashes light-blue when selected
                        v.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
}


