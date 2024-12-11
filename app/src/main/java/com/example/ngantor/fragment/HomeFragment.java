package com.example.ngantor.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.TimePickerDialog;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngantor.usecase.AlarmReceiver;
import com.example.ngantor.usecase.Calendar;
import com.example.ngantor.R;
import com.example.ngantor.usecase.SleepMode;
import com.example.ngantor.utils.PermissionHelper;
import com.example.ngantor.utils.fetch.SleepModeViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private boolean isRecording = false;
    private SleepModeViewModel sleepModeViewModel;

    private RecyclerView calendarRecyclerView;

    private ConstraintLayout sleepButtonBackground;
    private TextView sleepButtonText;

    private View bedtime_display;
    private View bedtime_edit;

    private SleepMode sleepMode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sleepMode = new SleepMode(requireContext());
        sleepModeViewModel = new ViewModelProvider(requireActivity()).get(SleepModeViewModel.class);

        calendarRecyclerView = view.findViewById(R.id.calendar_recycler_view);
        sleepButtonBackground = view.findViewById(R.id.start_sleep_button);
        sleepButtonText = view.findViewById(R.id.sleep_button_text);
        bedtime_display = view.findViewById(R.id.bedtime_display);
        bedtime_edit = view.findViewById(R.id.bedtime_edit);
        TextView alarmDisplay = view.findViewById(R.id.alarm_time_display);
        ImageButton alarmEdit = view.findViewById(R.id.alarm_edit);

        bedtime_edit.setOnClickListener(v -> showTimePickerDialog((TextView) bedtime_display));
        alarmEdit.setOnClickListener(v -> showTimePickerDialog(alarmDisplay));


        sleepModeViewModel.getIsRecording().observe(getViewLifecycleOwner(), recording -> {
            isRecording = recording;
            updateSleepButtonUI();
        });

        sleepButtonBackground.setOnClickListener(v -> {
            if (!PermissionHelper.checkAndRequestAudioPermission(requireContext(), requireActivity())) {
                Log.e("HomeFragment", "Microphone permission not granted.");
                Toast.makeText(requireContext(), "Please grant microphone permission", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isRecording) {
                sleepMode.StartSleep();
                isRecording = true;
                sleepModeViewModel.setIsRecording(true);
                Toast.makeText(requireContext(), "Sleep mode started", Toast.LENGTH_SHORT).show();
            } else {
                sleepMode.EndSleep();
                isRecording = false;
                sleepModeViewModel.setIsRecording(false);
                Toast.makeText(requireContext(), "Sleep mode ended", Toast.LENGTH_SHORT).show();
            }
        });




        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        calendarRecyclerView.setLayoutManager(layoutManager);

        Calendar calendarAdapter = new Calendar(date -> {
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


    private void updateSleepButtonUI() {
        if (isRecording) {
            sleepButtonText.setText("I'm Awake!");
            sleepButtonText.setTextColor(getResources().getColor(R.color.aqua));
            sleepButtonBackground.setBackgroundResource(R.drawable.container_stroke_gradient);
        } else {
            sleepButtonText.setText("Start Sleep");
            sleepButtonText.setTextColor(getResources().getColor(R.color.black));
            sleepButtonBackground.setBackgroundResource(R.drawable.container_solid_gradient);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permission", "Microphone permission granted");
                Toast.makeText(requireContext(), "Microphone permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Permission", "Microphone permission denied");
                Toast.makeText(requireContext(), "Microphone permission is required for sleep mode", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showTimePickerDialog(TextView displayView) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = calendar.get(java.util.Calendar.MINUTE);

        // Show TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    // Format and set the selected time
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    displayView.setText(formattedTime);

                    // Set Alarm
                    setAlarm(hourOfDay, minuteOfHour);
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    private void setAlarm(int hourOfDay, int minuteOfHour) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        // Create an Intent for the BroadcastReceiver
        Intent alarmIntent = new Intent(requireContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Set up the calendar
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(java.util.Calendar.MINUTE, minuteOfHour);
        calendar.set(java.util.Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
        }

        // Set the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}