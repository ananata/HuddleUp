package com.example.huddleup;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.huddleup.fragments.ForgotPasswordFragment;

import OtherClasses.AuthenticationPagerAdapter;

public class ForgotPasswordScreen extends AppCompatActivity {
    // Declare instance variables
    private TextView recoveryEmail;
    private Button getPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display Fragment
        showPasswordRecoveryFragment();
    }

    /**
     * Mount forgot password fragment onto activity
     */
    private void showPasswordRecoveryFragment() {
        setContentView(R.layout.activity_forgot_password_screen);
        ViewPager viewPager = findViewById(R.id.viewPagerRecovery);

        AuthenticationPagerAdapter pagerAdapter = new AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new ForgotPasswordFragment());
        viewPager.setAdapter(pagerAdapter);
    }


}
