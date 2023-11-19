package com.example.credisync;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class PrivacyPolicy extends AppCompatActivity {

    WebView webView;
    public String fileName = "https://www.termsfeed.com/live/252aa26c-9514-490b-9498-a423ca61813a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color

        webView = (WebView) findViewById(R.id.privacyWeb);
        webView.getSettings().setJavaScriptEnabled(true); //display content in web view from html file in assets folder
        //webView.loadUrl("file:///android_asset/" + fileName);
        webView.loadUrl(fileName);
    }

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}