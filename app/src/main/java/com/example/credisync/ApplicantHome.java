package com.example.credisync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.credisync.databinding.ActivityApplicantHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ApplicantHome extends AppCompatActivity {

    protected String userEmail;
    protected static final String TAG = "HomeActivity";
    protected FirebaseFirestore db;
    protected SharedPreferences sharedPreferences; //storage mechanism used for auth/login purposes

    private RecyclerView.Adapter adapterCoopList;
    private RecyclerView recyclerViewCoop; //at dashboard fragment
    protected TextView goodDay, points, creditScore, lastUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeRecyclerView();
    }

    protected void initializeRecyclerView() {

        //place here coop details
        ArrayList<CooperativesDomain> items = new ArrayList<>();

        //add available coops (implement here from signup in firebase??) items from cooperatives collection in firebase??
        items.add(new CooperativesDomain("Cebu CFI","Coop Bldg, Capitol Compound Road", "coopLogos"));
        items.add(new CooperativesDomain("MAVENCO","Coop Bldg, Capitol Compound Road", "coopLogos"));
        items.add(new CooperativesDomain("Coop Bank","Coop Bldg, Capitol Compound Road", "coopLogos"));
        items.add(new CooperativesDomain("People's Coop","Coop Bldg, Capitol Compound Road", "coopLogos"));
        items.add(new CooperativesDomain("Cebu People's","Coop Bldg, Capitol Compound Road", "coopLogos"));
        items.add(new CooperativesDomain("Cebu CFI","Coop Bldg, Capitol Compound Road", "coopLogos"));

        recyclerViewCoop = findViewById(R.id.dashboardRecyclerView); //find RecyclerView in the fragment's layout.
        recyclerViewCoop.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        adapterCoopList = new CooperativesAdapter(items);
        recyclerViewCoop.setAdapter(adapterCoopList);
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