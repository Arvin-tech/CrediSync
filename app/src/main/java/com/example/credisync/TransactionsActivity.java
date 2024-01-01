package com.example.credisync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class TransactionsActivity extends AppCompatActivity {

    protected LinearLayout homeLayout, notifLayout, profileLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        findViewById();
        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color

        //bottom navigation
        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(getApplicationContext(), ApplicantHome.class);
                startActivity(home);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); //slide to left
            }
        });

        notifLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notifications = new Intent(getApplicationContext(), NotificationsActivity.class);
                startActivity(notifications);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); //slide to left
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile  = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profile);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //slide to right
            }
        });
    }

    protected void findViewById(){
        homeLayout = findViewById(R.id.homeMenuLayout);
        notifLayout = findViewById(R.id.notifMenuLayout);
        profileLayout = findViewById(R.id.profileMenuLayout);
    }

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}