package com.example.prototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {

    private List<String> devicesList;
    private List<String> devicesIdList;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public DevicesAdapter(Context context, List<String> devicesList, OnItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.devicesList = devicesList;
        this.devicesIdList = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.grid_item_device, parent, false);
        return new DeviceViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        holder.deviceButton.setText(devicesList.get(position));
        //holder.deviceButton.setOnItemClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return devicesList.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        public Button deviceButton;
        public ImageButton powerButton;

        public DeviceViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            deviceButton = itemView.findViewById(R.id.deviceButton);
            powerButton = itemView.findViewById(R.id.powerButton);
            deviceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            powerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO close the device (set it to sleep)
                }
            });
        }
    }

    public void updateDevicesList(List<String> newDevicesList, List<String> newDevicesIdList) {
        devicesList.clear();
        devicesList.addAll(newDevicesList);
        devicesIdList.clear();
        devicesIdList.addAll(newDevicesIdList);
        notifyDataSetChanged();
    }

    public String getDeviceNameAtPosition(int position) {
        return devicesList.get(position);
    }

    public String getDeviceIdAtPosition(int position) {
        return devicesIdList.get(position);
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
