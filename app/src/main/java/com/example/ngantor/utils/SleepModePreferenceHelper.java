package com.example.ngantor.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SleepModePreferenceHelper {

    private static final String PREF_NAME = "sleep_mode_pref";
    private static final String KEY_IS_RECORDING = "is_recording";

    private SharedPreferences sharedPreferences;

    public SleepModePreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean getIsRecording() {
        return sharedPreferences.getBoolean(KEY_IS_RECORDING, false);
    }

    public void setIsRecording(boolean isRecording) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_RECORDING, isRecording);
        editor.apply();
    }
}
