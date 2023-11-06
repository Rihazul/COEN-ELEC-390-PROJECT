package com.example.prototype;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CameraFootageAdapter extends RecyclerView.Adapter<CameraFootageAdapter.VideoViewHolder> {
    private final List<CameraFootage> cameraFootageList;
    private final Context context;

    public CameraFootageAdapter(List<CameraFootage> cameraFootageList, Context context) {
        this.cameraFootageList = cameraFootageList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_item, viewGroup, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder videoViewHolder, int position) {
        CameraFootage cameraFootage = cameraFootageList.get(position);
        videoViewHolder.dateTextView.setText(cameraFootage.getDate());
        videoViewHolder.timeTextView.setText(cameraFootage.getTime());

        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(videoViewHolder.videoView);
        videoViewHolder.videoView.setMediaController(mediaController);
        videoViewHolder.videoView.setVideoURI(Uri.parse(cameraFootage.getURL()));
    }

    @Override
    public int getItemCount() {
        return cameraFootageList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoView videoView;
        public TextView dateTextView;
        public TextView timeTextView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}
