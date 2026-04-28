package com.example.thryve;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RunAdapter extends RecyclerView.Adapter<RunAdapter.ViewHolder> {

    private List<RunModel> runs;
    private OnItemClickListener listener;

    // 🔥 Click interface
    public interface OnItemClickListener {
        void onClick(RunModel run);
    }

    public RunAdapter(List<RunModel> runs, OnItemClickListener listener) {
        this.runs = runs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_run, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RunModel run = runs.get(position);

        //  Index
        holder.tvIndex.setText("#" + (position + 1) + " RUN");

        //  Distance
        holder.tvDistance.setText(String.format(Locale.US, "%.2f km", run.distance));

        //  Duration
        long sec = run.duration_sec;
        holder.tvDuration.setText(String.format(Locale.US, "%02d:%02d",
                sec / 60, sec % 60));

        // Date
        if (run.timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
            holder.tvDate.setText(sdf.format(run.timestamp.toDate()));
        }

        //  Click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(run);
            }
        });
    }

    @Override
    public int getItemCount() {
        return runs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvIndex, tvDistance, tvDuration, tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}