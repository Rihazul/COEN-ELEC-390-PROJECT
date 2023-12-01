package com.example.prototype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private final List<String> homeList;
    private final List<String> homeIdsList;
    private int selectedPosition = -1;
    private final String defaultHomeId;

    public HomeAdapter(List<String> homeList, List<String> homeIdsList, String defaultHomeId) {
        this.homeList = homeList;
        this.homeIdsList = homeIdsList;
        this.defaultHomeId = defaultHomeId;

        selectedPosition = getPositionFromId(defaultHomeId);
    }

    private int getPositionFromId(String homeId) {
        return homeIdsList.indexOf(homeId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String home = homeList.get(position);
        viewHolder.homeTextView.setText(home);

        viewHolder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });

        if (selectedPosition == position) {
            viewHolder.checkImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView homeTextView;
        public ImageView checkImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            homeTextView = itemView.findViewById(R.id.homeTextView);
            checkImageView = itemView.findViewById(R.id.checkImageView);
        }
    }
}