package com.example.ngantor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        calendarRecyclerView = view.findViewById(R.id.calendar_recycler_view);

        // Set up layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        calendarRecyclerView.setLayoutManager(layoutManager);

        // Initialize adapter with click listener
        calendarAdapter = new CalendarAdapter(date -> {
            // Handle date selection
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selectedDate = sdf.format(date.getTime());
            // You can replace this with your own date handling logic
            Toast.makeText(requireContext(),
                    "Selected date: " + selectedDate,
                    Toast.LENGTH_SHORT).show();
        });

        // Set adapter to RecyclerView
        calendarRecyclerView.setAdapter(calendarAdapter);

        // Add item decoration with hardcoded 8dp spacing
        calendarRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(
                (int) (8 * requireContext().getResources().getDisplayMetrics().density) // 8dp converted to pixels
        ));
    }

    // Custom ItemDecoration for horizontal spacing
    private static class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public HorizontalSpaceItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull android.graphics.Rect outRect,
                                   @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position != parent.getAdapter().getItemCount() - 1) {
                outRect.right = spacing;
            }
        }
    }
}