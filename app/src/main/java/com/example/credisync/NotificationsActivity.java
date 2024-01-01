package com.example.credisync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class NotificationsActivity extends AppCompatActivity {

    protected LinearLayout homeLayout, transactionsLayout, profileLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

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

        transactionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transactions = new Intent(getApplicationContext(), TransactionsActivity.class);
                startActivity(transactions);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //slide to right
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

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    protected void findViewById(){
        homeLayout = findViewById(R.id.homeMenuLayout);
        transactionsLayout = findViewById(R.id.transactionsMenuLayout);
        profileLayout = findViewById(R.id.profileMenuLayout);
    }

}