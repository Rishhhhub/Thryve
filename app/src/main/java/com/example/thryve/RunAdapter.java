package com.example.thryve;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RunAdapter extends RecyclerView.Adapter<RunAdapter.ViewHolder> {

    private List<RunModel> runs;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(RunModel run);
    }

    public RunAdapter(List<RunModel> runs, OnItemClickListener listener) {
        this.runs = runs;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_run, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        RunModel run = runs.get(position);

        // ✅ NEWEST = #1
        holder.tvIndex.setText("#" + (runs.size() - position) + " RUN");

        holder.tvDistance.setText(String.format(Locale.US, "%.2f km", run.distance));

        long sec = run.duration_sec;
        holder.tvDuration.setText(String.format(Locale.US, "%02d:%02d",
                sec / 60, sec % 60));

        if (run.timestamp != null) {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
            holder.tvDate.setText(sdf.format(run.timestamp.toDate()));
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(run));
    }

    @Override
    public int getItemCount() {
        return runs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvIndex, tvDistance, tvDuration, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}