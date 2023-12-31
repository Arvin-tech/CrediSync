package com.example.credisync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;

import java.util.ArrayList;

public class CooperativesAdapter extends RecyclerView.Adapter<CooperativesAdapter.Viewholder> {

    ArrayList<CooperativesDomain> items;
    Context context;

    public CooperativesAdapter(ArrayList<CooperativesDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CooperativesAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_coops_available,parent,false);
        context = parent.getContext();
        return new Viewholder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull CooperativesAdapter.Viewholder holder, int position) {
        holder.coopName.setText(items.get(position).getCoopName());
        holder.coopAddress.setText(items.get(position).getCoopAddress());

        int drawableResourceId=holder.itemView.getResources().getIdentifier(items.get(position).getPicAddress(),"drawable",holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30,30,0,0))
                .into(holder.coopLogo);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        TextView coopName, coopAddress;
        ImageView coopLogo;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            coopName = itemView.findViewById(R.id.coopNameTextView);
            coopAddress = itemView.findViewById(R.id.coopAddressTextView);
            coopLogo = itemView.findViewById(R.id.coopLogoImageView);
        }
    }
}
