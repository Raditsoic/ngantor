package com.example.ngantor.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngantor.usecase.Calendar;
import com.example.ngantor.R;
import com.example.ngantor.usecase.DecibelSensor;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class HomeFragment extends Fragment implements DecibelSensor.DecibelMeasureListener {
    private DecibelSensor decibelMeasureHelper;
    private boolean isRecording = false;
    private RecyclerView calendarRecyclerView;
    private Calendar calendarAdapter;
    private ConstraintLayout sleepButtonBackground;
    private TextView sleepButtonText;
    private TextView decibelReadingText;
    private View bedtime_display;
    private View bedtime_edit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        decibelMeasureHelper = new DecibelSensor(requireContext(), this);

        calendarRecyclerView = view.findViewById(R.id.calendar_recycler_view);
        sleepButtonBackground = view.findViewById(R.id.start_sleep_button);
        sleepButtonText = view.findViewById(R.id.sleep_button_text);
        decibelReadingText = view.findViewById(R.id.decibel_reading_text);

        bedtime_display = view.findViewById(R.id.bedtime_display);
        bedtime_edit = view.findViewById(R.id.bedtime_edit);
        TextView alarmDisplay = view.findViewById(R.id.alarm_time_display);
        ImageButton alarmEdit = view.findViewById(R.id.alarm_edit);

        bedtime_edit.setOnClickListener(v -> showTimePickerDialog((TextView) bedtime_display));
        alarmEdit.setOnClickListener(v -> showTimePickerDialog(alarmDisplay));

        sleepButtonBackground.setOnClickListener(v -> {
            if (!isRecording) {
                startSoundMeasurement();
            } else {
                stopSoundMeasurement();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        calendarRecyclerView.setLayoutManager(layoutManager);

        calendarAdapter = new Calendar(date -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selectedDate = sdf.format(date.getTime());
            Toast.makeText(requireContext(),
                    "Selected date: " + selectedDate,
                    Toast.LENGTH_SHORT).show();
        });

        calendarRecyclerView.setAdapter(calendarAdapter);

        calendarRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(
                (int) (8 * requireContext().getResources().getDisplayMetrics().density)
        ));

        calendarRecyclerView.post(() -> {
            int offset = (calendarRecyclerView.getWidth() / 2) -
                    (int) (56 * requireContext().getResources().getDisplayMetrics().density); // Approximate item width
            ((LinearLayoutManager) calendarRecyclerView.getLayoutManager())
                    .scrollToPositionWithOffset(30, offset);
        });
    }


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

    private void startSoundMeasurement() {
        // Check and request permissions if needed
        if (decibelMeasureHelper.checkAudioPermissions(requireContext())) {
            if (decibelMeasureHelper.startSoundMeasurement(requireContext())) {
                updateSleepButtonUI(true);
            }
        } else {
            // Request permissions (you'll need to implement this)
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 100);
        }
    }

    private void stopSoundMeasurement() {
        decibelMeasureHelper.stopSoundMeasurement();
        updateSleepButtonUI(false);
    }

    private void updateSleepButtonUI(boolean isRecording) {
        this.isRecording = isRecording; // Add this line to toggle the recording state

        if (isRecording) {
            sleepButtonText.setText("I'm Awake!");
            sleepButtonText.setTextColor(getResources().getColor(R.color.aqua));
            sleepButtonBackground.setBackgroundResource(R.drawable.container_stroke_gradient);
        } else {
            sleepButtonText.setText("Start Sleep");
            sleepButtonText.setTextColor(getResources().getColor(R.color.black));
            sleepButtonBackground.setBackgroundResource(R.drawable.container_solid_gradient);

            if (decibelReadingText != null) {
                decibelReadingText.setText("--");
            }
        }
    }

    @Override
    public void onDecibelUpdate(double decibels) {
        // Update UI with decibel reading
        if (decibelReadingText != null) {
            getActivity().runOnUiThread(() -> {
                decibelReadingText.setText(String.format("%.1f dB", decibels));
            });
        }
    }

    @Override
    public void onMeasurementError(String errorMessage) {
        // Show error to user
        getActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            updateSleepButtonUI(false);
        });
    }

    @Override
    public void onMeasurementStopped() {
        getActivity().runOnUiThread(() -> {
            updateSleepButtonUI(false);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (decibelMeasureHelper != null) {
            decibelMeasureHelper.stopSoundMeasurement();
        }
    }

    private void showTimePickerDialog(TextView displayView) {
        // Get current time as default
        java.util.Calendar calendar = java.util.Calendar.getInstance(); // Fully qualify the class
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = calendar.get(java.util.Calendar.MINUTE);

        // Show TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    // Format and set the selected time
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    displayView.setText(formattedTime);
                },
                hour, minute, true); // Use 24-hour format
        timePickerDialog.show();
    }





}