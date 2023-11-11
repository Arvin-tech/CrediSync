package com.example.credisync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class UserSignupPrompt extends AppCompatActivity {

    protected ImageView coopImageView, applicantImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup_prompt);

        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color
        findViewById(); //reference to ui elements

        //coop signup activity
        coopImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(), CoopSignup.class);
                //startActivity(intent); //redirect to coop signup (step 2)
            }
        });

        //applicant signup activity
        applicantImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ApplicantSignup.class);
                startActivity(intent); //redirect to applicant signup (step 1)
            }
        });
    }

    //reference to ui elements
    protected void findViewById(){
        coopImageView = findViewById(R.id.financialCoopImg);
        applicantImageView = findViewById(R.id.applicantImg);
    }

    //set status bar color
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}