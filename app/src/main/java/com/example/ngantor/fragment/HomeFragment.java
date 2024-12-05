package com.example.ngantor.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.ngantor.usecase.SleepMode;
import com.example.ngantor.utils.PermissionHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private boolean isRecording = false;
    private RecyclerView calendarRecyclerView;

    private ConstraintLayout sleepButtonBackground;
    private TextView sleepButtonText;
    private TextView decibelReadingText;

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

        calendarRecyclerView = view.findViewById(R.id.calendar_recycler_view);
        sleepButtonBackground = view.findViewById(R.id.start_sleep_button);
        sleepButtonText = view.findViewById(R.id.sleep_button_text);

        sleepButtonBackground.setOnClickListener(v -> {
            if (!PermissionHelper.checkAndRequestAudioPermission(requireContext(), requireActivity())) {
                Log.e("HomeFragment", "Microphone permission not granted.");
                Toast.makeText(requireContext(), "Please grant microphone permission", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isRecording) {
                sleepMode.StartSleep();
                isRecording = true;
                updateSleepButtonUI();
                Toast.makeText(requireContext(), "Sleep mode started", Toast.LENGTH_SHORT).show();
            } else {
                sleepMode.EndSleep();
                isRecording = false;
                updateSleepButtonUI();
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

            if (decibelReadingText != null) {
                decibelReadingText.setText("--");
            }
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
}