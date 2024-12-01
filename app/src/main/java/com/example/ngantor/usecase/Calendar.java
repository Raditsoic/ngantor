package com.example.ngantor.usecase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngantor.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Calendar extends RecyclerView.Adapter<Calendar.CalendarViewHolder> {
    private List<java.util.Calendar> dateList;
    private int selectedPosition = -1;
    private OnDateSelectedListener listener;

    public interface OnDateSelectedListener {
        void onDateSelected(java.util.Calendar date);
    }

    public Calendar(OnDateSelectedListener listener) {
        this.listener = listener;
        dateList = new ArrayList<>();
        generateDateList();
    }

    private void generateDateList() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();

        int days_after_before = 7;

        for (int i = -(days_after_before); i < 0; i++) {
            java.util.Calendar date = (java.util.Calendar) calendar.clone();
            date.add(java.util.Calendar.DAY_OF_YEAR, i);
            dateList.add(date);
        }

        dateList.add((java.util.Calendar) calendar.clone());

        for (int i = 1; i <= days_after_before; i++) {
            java.util.Calendar date = (java.util.Calendar) calendar.clone();
            date.add(java.util.Calendar.DAY_OF_YEAR, i);
            dateList.add(date);
        }

        selectedPosition = days_after_before;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_item, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        java.util.Calendar date = dateList.get(position);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());

        holder.dayText.setText(dayFormat.format(date.getTime()));
        holder.dateText.setText(dateFormat.format(date.getTime()));
        holder.monthText.setText(monthFormat.format(date.getTime()));

        holder.itemView.setSelected(selectedPosition == position);

        java.util.Calendar today = java.util.Calendar.getInstance();
        boolean isToday = today.get(java.util.Calendar.YEAR) == date.get(java.util.Calendar.YEAR)
                && today.get(java.util.Calendar.DAY_OF_YEAR) == date.get(java.util.Calendar.DAY_OF_YEAR);
        holder.itemView.setEnabled(isToday);

        holder.itemView.setOnClickListener(v -> {
            int newPosition = holder.getAdapterPosition();
            if (newPosition != RecyclerView.NO_POSITION) {
                int previousPosition = selectedPosition;
                selectedPosition = newPosition;
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                listener.onDateSelected(dateList.get(newPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        TextView dateText;
        TextView monthText;

        CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            dateText = itemView.findViewById(R.id.dateText);
            monthText = itemView.findViewById(R.id.monthText);
        }
    }
}