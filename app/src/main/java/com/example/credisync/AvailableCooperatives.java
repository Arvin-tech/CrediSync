package com.example.credisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;

public class AvailableCooperatives extends AppCompatActivity {

    protected RecyclerView.Adapter adapterCoopList;
    protected RecyclerView recyclerViewCoop;
    protected ImageView backImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_cooperatives);

        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color

        recyclerViewCoop = findViewById(R.id.coopListRecyclerView); //find RecyclerView in the available coops activity
        backImage = (ImageView) findViewById(R.id.backArrowImageView);
        initializeRecycleView();

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ApplicantHome.class);
                startActivity(intent);
            }
        });
    }

    private void initializeRecycleView() {
        String cfiLogo = String.valueOf(R.drawable.cficooperative);
        String mavencoLogo = String.valueOf(R.drawable.mavenco);
        String cebuPeoplesLogo = String.valueOf(R.drawable.cpmpc);
        String coopBankLogo = String.valueOf(R.drawable.cboc);

        //place here coop details
        ArrayList<CooperativesDomain> items = new ArrayList<>();

        //add available coops (implement here from signup in firebase??) items from cooperatives collection in firebase??
        items.add(new CooperativesDomain("Mandaue City Public Market Vendors Multi-Purpose Coop","Coop Bldg, Capitol Compound Road" ,mavencoLogo));
        items.add(new CooperativesDomain("Cebu CFI Community Cooperative","Coop Bldg, Capitol Compound Road" ,cfiLogo));
        items.add(new CooperativesDomain("Cebu People's Multi-Purpose Cooperative","Coop Bldg, Capitol Compound Road" ,cebuPeoplesLogo));
        items.add(new CooperativesDomain("Cooperative Bank of Cebu","Coop Bldg, Capitol Compound Road" ,coopBankLogo));

        recyclerViewCoop.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapterCoopList = new AvailableCooperativesAdapter(items);
        recyclerViewCoop.setAdapter(adapterCoopList);
    }

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

}