package com.example.ngantor.usecase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngantor.R;
import com.example.ngantor.data.models.SleepSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SleepLogAdapter extends RecyclerView.Adapter<SleepLogAdapter.SleepViewHolder> {

    private List<SleepSession> sleepSessions;
    private OnSleepLogClickListener clickListener;

    public interface OnSleepLogClickListener {
        void onSleepLogClick(SleepSession sleepLog);
    }

    public SleepLogAdapter(List<SleepSession> sleepSessions, OnSleepLogClickListener clickListener) {
        this.sleepSessions = sleepSessions;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SleepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log, parent, false);
        return new SleepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SleepViewHolder holder, int position) {
        SleepSession session = sleepSessions.get(position);

        String formattedDate = formatDate(session.getStartTime());
        holder.dateTextView.setText(formattedDate);

        holder.ratingTextView.setText("Sleep Quality: " + formatQuality(session.getSleepQuality()) + "%");

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onSleepLogClick(session);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sleepSessions != null ? sleepSessions.size() : 0;
    }

    public void updateData(List<SleepSession> newSessions) {
        if (newSessions != null) {
            this.sleepSessions = newSessions;
            notifyDataSetChanged();
        }
    }

    public int formatQuality(float quality) {
        return (int) (quality * 100);
    }

    static class SleepViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView ratingTextView;

        public SleepViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
        }
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return formatter.format(new Date(timestamp));
    }
}
