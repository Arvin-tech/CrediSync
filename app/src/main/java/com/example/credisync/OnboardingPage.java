package com.example.credisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnboardingPage extends AppCompatActivity {

    Animation topAnimation;
    ImageView logo; //credi sync logo
    protected static int SPLASH_SCREEN = 5000;
    protected AppCompatButton loginButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //Hide status bar
        setContentView(R.layout.activity_onboarding_page);

        findViewById(); //reference to ui elements
        setStatusBarColor(getResources().getColor(R.color.light_gray));

        topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_anim); //find xml
        logo = findViewById(R.id.imageView2);
        logo.setAnimation(topAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }

    protected void findViewById(){

    }

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}