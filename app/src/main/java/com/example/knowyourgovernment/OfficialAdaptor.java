package com.example.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdaptor extends RecyclerView.Adapter<OfficialViewHolder> {
    private List<Official> officialList;
    private MainActivity mainActivity;

    public OfficialAdaptor(List<Official> officialList, MainActivity mainActivity) {
        this.officialList = officialList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_row, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Official o=officialList.get(position);

        holder.title.setText(o.getTitle());
        holder.name.setText(o.getName());
        holder.party.setText(" ("+o.getParty().split(" ")[0]+") ");
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
