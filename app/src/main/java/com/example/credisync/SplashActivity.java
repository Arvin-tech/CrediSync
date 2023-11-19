package com.example.credisync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.credisync.screens.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // splash screen wait for 2 sec and then launch login activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    // if user already login then it will go to home screen
                    // he do not need to login again
                    startActivity(new Intent(SplashActivity.this, OnboardingPage.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, OnboardingPage.class));
                }
            }
        },3000);
    }
}