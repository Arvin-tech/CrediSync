package com.example.credisync;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ProfileFragment extends Fragment {

    protected CardView profile;
    protected LinearLayout transaction, creditReport, creditHelp, feedback, terms, privacy, about, deleteAccount, logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(); //method call

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Profile.class); //redirect to profile activity
                startActivity(intent);
            }
        });
    }

    public void findViewById(){
        // Inflate the layout for this fragment
        View view = getView();
        if (view != null) {
            profile = view.findViewById(R.id.cardView2);
            transaction = view.findViewById(R.id.transactionHistoryLinearLayout);
            creditReport = view.findViewById(R.id.creditReportsLinearLayout);
            creditHelp = view.findViewById(R.id.creditHelpLinearLayout);
            feedback = view.findViewById(R.id.feedbackLinearLayout);
            terms = view.findViewById(R.id.termsConditionsLinearLayout);
            privacy = view.findViewById(R.id.privacyLinearLayout);
            about = view.findViewById(R.id.aboutLinearLayout);
            deleteAccount = view.findViewById(R.id.deleteAccountLinearLayout);
            logout = view.findViewById(R.id.logoutLinearLayout);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}