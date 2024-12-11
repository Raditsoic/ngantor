package com.example.ngantor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngantor.LogDetailActivity;
import com.example.ngantor.R;
import com.example.ngantor.data.repositories.SleepSessionRepository;
import com.example.ngantor.usecase.SleepLogAdapter;
import com.example.ngantor.utils.fetch.SleepViewModel;
import com.example.ngantor.utils.fetch.SleepViewModelFactory;

import java.util.ArrayList;

public class LogsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SleepLogAdapter adapter;
    private SleepViewModel sleepViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.sleepLogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new SleepLogAdapter(new ArrayList<>(), sleepSession -> {
            Intent intent = new Intent(requireContext(), LogDetailActivity.class);
            intent.putExtra("SLEEP_SESSION_ID", sleepSession.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        sleepViewModel = new ViewModelProvider(
                this,
                new SleepViewModelFactory(new SleepSessionRepository(requireContext()))
        ).get(SleepViewModel.class);

        sleepViewModel.getAllSleepSessions().observe(getViewLifecycleOwner(), sleepSessions -> {
            if (sleepSessions != null) {
                adapter.updateData(sleepSessions); // Update adapter with new data
            }
        });
    }


}
