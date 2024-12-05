package com.example.ngantor.utils.fetch;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ngantor.data.repositories.SleepSessionRepository;

public class SleepViewModelFactory implements ViewModelProvider.Factory {

    private final SleepSessionRepository repository;

    public SleepViewModelFactory(SleepSessionRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SleepViewModel.class)) {
            return (T) new SleepViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
