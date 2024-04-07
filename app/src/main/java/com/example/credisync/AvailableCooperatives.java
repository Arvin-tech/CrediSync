package com.example.credisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;

public class AvailableCooperatives extends AppCompatActivity {

    protected AvailableCooperativesAdapter availablecooperativesAdapter;
    protected RecyclerView recyclerViewCoop;
    protected ArrayList<CooperativesDomain> items;
    protected ImageView backImage;

    private int space;

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

        //card cover
        String cfiLogo = String.valueOf(R.drawable.cfipic);
        String mavencoLogo = String.valueOf(R.drawable.mavencopic);
        String cebuPeoplesLogo = String.valueOf(R.drawable.cebupeoplepic);
        String coopBankLogo = String.valueOf(R.drawable.cbocpic);
        String tayemcoLogo = String.valueOf(R.drawable.tayemcopic);

        //card logo
        String cfi = String.valueOf(R.drawable.cficooperative);
        String mavenco = String.valueOf(R.drawable.mavenco);
        String cebupeople = String.valueOf(R.drawable.cpmpc);
        String cboc = String.valueOf(R.drawable.cboc);

        //place here coop details
        items = new ArrayList<>();
        //add available coops (implement here from signup in firebase??) items from cooperatives collection in firebase??
        items.add(new CooperativesDomain("Cebu CFI","Coop Bldg, Capitol Compound Road", cfiLogo, cfi));
        items.add(new CooperativesDomain("MAVENCO","National Highway, Consolacion, Cebu", mavencoLogo, mavenco));
        items.add(new CooperativesDomain("Cooperative Bank of Cebu","CBOC Bldg.,G/F, 30 M. Velez St, Cebu City, Cebu", coopBankLogo, cboc));
        items.add(new CooperativesDomain("People's Coop","16 Salinas Drive, Apas, Lahug, Cebu City", cebuPeoplesLogo,cebupeople));
        items.add(new CooperativesDomain("TAYEMCO","Coop Bldg, Capitol Compound Road", tayemcoLogo, cfi)); //temporary logo

        recyclerViewCoop.setLayoutManager(new LinearLayoutManager(this));
        availablecooperativesAdapter = new AvailableCooperativesAdapter(items);
        recyclerViewCoop.setAdapter(availablecooperativesAdapter);

    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

}