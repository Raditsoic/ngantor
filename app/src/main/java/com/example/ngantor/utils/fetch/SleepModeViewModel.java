package com.example.ngantor.utils.fetch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SleepModeViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isRecording = new MutableLiveData<>(false);

    public LiveData<Boolean> getIsRecording() {
        return isRecording;
    }

    public void setIsRecording(boolean recording) {
        isRecording.setValue(recording);
    }
}
