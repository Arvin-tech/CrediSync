package com.example.credisync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ApplicantHome extends AppCompatActivity {

    protected String userEmail;
    protected static final String TAG = "HomeActivity";
    protected FirebaseFirestore db;
    protected SharedPreferences sharedPreferences; //storage mechanism used for auth/login purposes

    private RecyclerView.Adapter adapterCoopList;
    private RecyclerView recyclerViewCoop; //recycler view in this activity
    protected TextView goodDayTxt, pointsTxt, creditScoreTxt, lastUpdatedTxt, seeAllTxt;
    protected LinearLayout notifLayout, transactionsLayout, profileLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_home);

        findViewById();
        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color

        recyclerViewCoop = findViewById(R.id.dashboardRecyclerView); //find RecyclerView in the fragment's layout.
        initializeRecyclerView();

        seeAllTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AvailableCooperatives.class);
                startActivity(intent); //opens available coops activity
            }
        });

        //bottom navigation
        notifLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notifications = new Intent(getApplicationContext(), NotificationsActivity.class);
                startActivity(notifications);
            }
        });

        transactionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transactions = new Intent(getApplicationContext(), TransactionsActivity.class);
                startActivity(transactions);
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile  = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profile);
            }
        });

    }

    protected void findViewById(){
        seeAllTxt = findViewById(R.id.seeAllTextView);
        notifLayout = findViewById(R.id.notifMenuLayout);
        transactionsLayout = findViewById(R.id.transactionsMenuLayout);
        profileLayout = findViewById(R.id.profileMenuLayout);
    }

    protected void initializeRecyclerView() {
        String cfiLogo = String.valueOf(R.drawable.cficooperative);
        String mavencoLogo = String.valueOf(R.drawable.mavenco);
        String cebuPeoplesLogo = String.valueOf(R.drawable.cpmpc);
        String coopBankLogo = String.valueOf(R.drawable.cboc);

        //place here coop details
        ArrayList<CooperativesDomain> items = new ArrayList<>();

        //add available coops (implement here from signup in firebase??) items from cooperatives collection in firebase??
        items.add(new CooperativesDomain("Cebu CFI","Coop Bldg, Capitol Compound Road", cfiLogo));
        items.add(new CooperativesDomain("MAVENCO","Coop Bldg, Capitol Compound Road", mavencoLogo));
        items.add(new CooperativesDomain("Coop Bank","Coop Bldg, Capitol Compound Road", coopBankLogo));
        items.add(new CooperativesDomain("People's Coop","Coop Bldg, Capitol Compound Road", cebuPeoplesLogo));
        items.add(new CooperativesDomain("TAYEMCO","Coop Bldg, Capitol Compound Road", cfiLogo));

        recyclerViewCoop.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        adapterCoopList = new CooperativesAdapter(items);
        recyclerViewCoop.setAdapter(adapterCoopList);
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