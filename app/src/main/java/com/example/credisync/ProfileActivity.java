package com.example.credisync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class ProfileActivity extends AppCompatActivity {

    protected LinearLayout homeLayout, notificationLayout, transactionsLayout, changePasswordLayout, transactionsHistoryLayout, creditReportsLayout, creditHelpLayout, feedbackLayout, termsLayout, privacyLayout, aboutLayout, deleteLayout, logoutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById();
        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color

        //bottom navigation activities
        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(getApplicationContext(), ApplicantHome.class);
                startActivity(home);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); //slide to left
            }
        });

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notifications = new Intent(getApplicationContext(), NotificationsActivity.class);
                startActivity(notifications);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); //slide to left
            }
        });

        transactionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transactions = new Intent(getApplicationContext(), TransactionsActivity.class);
                startActivity(transactions);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); //slide to left\
            }
        });

        //settings activities
        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changePassword = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(changePassword);
            }
        });

        transactionsHistoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transactions = new Intent(getApplicationContext(), TransactionsActivity.class);
                startActivity(transactions);
            }
        });


    }

    protected void findViewById(){
        //bottom nav
        homeLayout = findViewById(R.id.homeMenuLayout);
        notificationLayout = findViewById(R.id.notifMenuLayout);
        transactionsLayout = findViewById(R.id.transactionsMenuLayout);
        //settings
        changePasswordLayout = findViewById(R.id.changePasswordLinearLayout);
        transactionsHistoryLayout = findViewById(R.id.transactionHistoryLinearLayout);
        creditReportsLayout =  findViewById(R.id.creditReportsLinearLayout);
        creditHelpLayout = findViewById(R.id.creditHelpLinearLayout);
        feedbackLayout = findViewById(R.id.feedbackLinearLayout);
        termsLayout = findViewById(R.id.termsConditionsLinearLayout);
        privacyLayout = findViewById(R.id.privacyLinearLayout);
        aboutLayout = findViewById(R.id.aboutLinearLayout);
        deleteLayout = findViewById(R.id.deleteAccountLinearLayout);
        logoutLayout = findViewById(R.id.logoutLinearLayout);

    }

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}