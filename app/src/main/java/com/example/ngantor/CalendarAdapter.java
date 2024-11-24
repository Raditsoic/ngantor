package com.example.ngantor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private List<Calendar> dateList;
    private int selectedPosition = -1;
    private OnDateSelectedListener listener;

    public interface OnDateSelectedListener {
        void onDateSelected(Calendar date);
    }

    public CalendarAdapter(OnDateSelectedListener listener) {
        this.listener = listener;
        dateList = new ArrayList<>();
        generateDateList();
    }

    private void generateDateList() {
        Calendar calendar = Calendar.getInstance();

        // Add previous 30 days
        for (int i = -30; i < 0; i++) {
            Calendar date = (Calendar) calendar.clone();
            date.add(Calendar.DAY_OF_YEAR, i);
            dateList.add(date);
        }

        // Add current day
        dateList.add((Calendar) calendar.clone());

        // Add next 30 days
        for (int i = 1; i <= 30; i++) {
            Calendar date = (Calendar) calendar.clone();
            date.add(Calendar.DAY_OF_YEAR, i);
            dateList.add(date);
        }

        // Set initial selection to current date (position 30)
        selectedPosition = 30;
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
        Calendar date = dateList.get(position);

        // Format date
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());

        holder.dayText.setText(dayFormat.format(date.getTime()));
        holder.dateText.setText(dateFormat.format(date.getTime()));
        holder.monthText.setText(monthFormat.format(date.getTime()));

        // Handle selection state
        holder.itemView.setSelected(selectedPosition == position);

        holder.itemView.setOnClickListener(v -> {
            // Get the current position when click happens
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