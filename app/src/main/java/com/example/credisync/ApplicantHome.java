package com.example.credisync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.credisync.databinding.ActivityApplicantHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class ApplicantHome extends AppCompatActivity {

    protected String userEmail;
    protected static final String TAG = "HomeActivity";
    protected FirebaseFirestore db;
    protected SharedPreferences sharedPreferences; //storage mechanism used for auth/login purposes
    ActivityApplicantHomeBinding binding; //binding is name of the class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApplicantHomeBinding.inflate(getLayoutInflater()); //for fragments
        setContentView(binding.getRoot()); //setContentView(R.layout.activity_home);
        replaceFragment(new DashboardFragment()); //default view

        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color
        db = FirebaseFirestore.getInstance(); //initialize fire store
        sharedPreferences = getSharedPreferences("auth_data", Context.MODE_PRIVATE);
        getUserDetails(); //grab the email

        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottom_home) {
                    replaceFragment(new DashboardFragment());
                } else if (item.getItemId() == R.id.bottom_notification) {
                    replaceFragment(new NotificationsFragment());
                } else if (item.getItemId() == R.id.bottom_profile) {
                    replaceFragment(new ProfileFragment());
                }
                return true;
            }
        });
    }

    protected void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("userEmail", userEmail); // Pass the user's email to the fragment
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }


    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    protected void saveAuthenticationState(boolean isAuthenticated) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_authenticated", isAuthenticated);
        editor.apply();
    }

    protected void getUserDetails(){
        // Get the email from the Intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userEmail = extras.getString("userEmail"); //key,  from forgot pass activity stored in userEmail
        }
    }
}