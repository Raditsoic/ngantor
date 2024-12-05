package com.example.ngantor.usecase;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class DecibelSensor {
    private static final String TAG = "DecibelSensor";

    private MediaRecorder mediaRecorder;
    private boolean isRecording;
    private int zeroReadingCount;
    private final Context mContext;

    public DecibelSensor(Context context) {
        this.mContext = context.getApplicationContext();
        isRecording = false;
        zeroReadingCount = 0;
    }

    public boolean start() {
        if (isRecording) return false;

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        try {
            zeroReadingCount = 0;
            File outputFile = new File(mContext.getCacheDir(), "temp_audio_recording.3gp");

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(outputFile.getAbsolutePath());

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            return true;
        } catch (SecurityException se) {
            Log.e(TAG, "Permission denied for audio recording", se);
        } catch (IOException | RuntimeException e) {
            Log.e(TAG, "Failed to start decibel measurement", e);
        }

        stop();
        return false;
    }

    public double getDecibelLevel() {
        if (!isRecording || mediaRecorder == null) return 0;

        try {
            double amplitude = mediaRecorder.getMaxAmplitude();

            Log.d(TAG, "Raw Amplitude: " + amplitude);

            if (amplitude > 0) {
                zeroReadingCount = 0;

                double decibels = 20 * Math.log10(amplitude);

                Log.i(TAG, String.format("Sound Level: %.1f dB (Amplitude: %.1f)",
                        decibels, amplitude));

                return decibels;
            } else {
                zeroReadingCount++;
                if (zeroReadingCount >= 10) {
                    Log.w(TAG, "Too many zero readings. Stopping measurement.");
                    stop();
                }
                return 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while measuring decibel level", e);
            stop();
            return 0;
        }
    }

    public void stop() {
        if (!isRecording) return;

        isRecording = false;

        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping MediaRecorder", e);
            }
            mediaRecorder = null;
        }

        Log.i(TAG, "Decibel measurement stopped");
    }
}