package com.example.huddleup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.huddleup.auth.and.database.Auth;

import OtherClasses.VisualUtilities;

public class UnverifiedEmailScreen extends AppCompatActivity {

    Button resend, goToLogin;
    VisualUtilities vu = new VisualUtilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unverified_email_screen);

        // Add action to buttons
        resend = findViewById(R.id.ButtonSendVerification);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.instance.firebaseUser.sendEmailVerification();
            }
        });
        goToLogin = findViewById(R.id.ButtonGoFromUnverifiedEmailToLogin);
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UnverifiedEmailScreen.this, MainActivity.class);
                UnverifiedEmailScreen.this.startActivity(intent);
            }
        });
    }
}
