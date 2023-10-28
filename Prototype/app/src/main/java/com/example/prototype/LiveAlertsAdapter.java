package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LiveAlertsAdapter extends RecyclerView.Adapter<LiveAlertsAdapter.ViewHolder> {
    private final List<LiveAlert> liveAlertList;
    private final Context context;

    public LiveAlertsAdapter(List<LiveAlert> liveAlertList, Context context) {
        this.liveAlertList = liveAlertList;
        this.context = context;
    }

    @NonNull
    @Override
    public LiveAlertsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.live_alert_item, viewGroup, false);
        return new LiveAlertsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveAlertsAdapter.ViewHolder viewHolder, int position) {
        LiveAlert liveAlert = liveAlertList.get(position);

        viewHolder.dateTextView.setText(liveAlert.getVideo().getDate());
        viewHolder.timeTextView.setText(liveAlert.getVideo().getTime());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LiveAlertsDetailsActivity.class);
                intent.putExtra("videoURL", liveAlert.getVideo().getURL());
                intent.putExtra("dateVideo", liveAlert.getVideo().getDate());
                intent.putExtra("timeVideo", liveAlert.getVideo().getTime());
                intent.putExtra("dateMotionSensor", liveAlert.getMotionSensor().getDate());
                intent.putExtra("timeMotionSensor", liveAlert.getMotionSensor().getTime());
                intent.putExtra("dateUSSensor", liveAlert.getUsSensor().getDate());
                intent.putExtra("timeUSSensor", liveAlert.getUsSensor().getTime());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return liveAlertList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}