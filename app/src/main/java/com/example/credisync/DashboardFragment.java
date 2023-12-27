package com.example.credisync;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private RecyclerView.Adapter adapterCoopList;
    private RecyclerView recyclerViewCoop; //at dashboard fragment
    protected TextView goodDay, points, creditScore, lastUpdated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add here for manual bottom nav view (not using menu xml)
    }

    private void initializeRecyclerView() {

        //place here coop details
        ArrayList<CooperativesDomain> items = new ArrayList<>();

        //add available coops (implement here from signup in firebase??) items from cooperatives collection in firebase??
        items.add(new CooperativesDomain("Cebu CFI","Coop Bldg, Capitol Compound Road", "coopLogos"));
        items.add(new CooperativesDomain("MAVENCO","Coop Bldg, Capitol Compound Road", "coopLogos"));
        items.add(new CooperativesDomain("Coop Bank","Coop Bldg, Capitol Compound Road", "coopLogos"));
        items.add(new CooperativesDomain("People's Coop","Coop Bldg, Capitol Compound Road", "coopLogos"));
        items.add(new CooperativesDomain("Cebu People's","Coop Bldg, Capitol Compound Road", "coopLogos"));
        items.add(new CooperativesDomain("Cebu CFI","Coop Bldg, Capitol Compound Road", "coopLogos"));

        recyclerViewCoop = requireView().findViewById(R.id.dashboardRecyclerView); //find RecyclerView in the fragment's layout.
        recyclerViewCoop.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        adapterCoopList = new CooperativesAdapter(items);
        recyclerViewCoop.setAdapter(adapterCoopList);
    }

    public void findViewById(){
        //goodDay = (TextView) findViewById(R.id.gooDayUserTxt);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeRecyclerView();
    }
}