package com.example.ngantor.usecase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the vibrator service
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Check if vibrator exists and is enabled
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibrate for 1 second
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For newer Android versions
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // For older Android versions
                vibrator.vibrate(1000);
            }
        }
    }
}
