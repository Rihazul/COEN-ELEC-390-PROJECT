package com.example.prototype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {
    private final List<Sensor> sensorList;

    public SensorAdapter(List<Sensor> sensorList) {
        this.sensorList = sensorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sensor_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Sensor sensor = sensorList.get(position);
        viewHolder.sensorValueTextView.setText(sensor.getDistance());
        viewHolder.dateTextView.setText(sensor.getDate());
        viewHolder.timeTextView.setText(sensor.getTime());
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sensorValueTextView;
        public TextView dateTextView;
        public TextView timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sensorValueTextView = itemView.findViewById(R.id.sensorValueTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}