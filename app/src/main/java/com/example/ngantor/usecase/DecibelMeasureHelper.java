package com.example.ngantor.usecase;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.util.Log;
import androidx.core.content.ContextCompat;
import java.io.IOException;

public class DecibelMeasureHelper {
    private static final String TAG = "DecibelMeasureHelper";
    private static final int SAMPLE_INTERVAL = 500; // milliseconds
    private static final int MAX_ZERO_READINGS = 10;

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private int zeroReadingCount = 0;
    private DecibelMeasureListener listener;

    public interface DecibelMeasureListener {
        void onDecibelUpdate(double decibels);
        void onMeasurementError(String errorMessage);
        void onMeasurementStopped();
    }

    public DecibelMeasureHelper(DecibelMeasureListener listener) {
        this.listener = listener;
    }

    public boolean checkAudioPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean startSoundMeasurement(Context context) {
        if (isRecording) return false;

        if (!checkAudioPermissions(context)) {
            listener.onMeasurementError("Audio recording permission not granted");
            return false;
        }

        try {
            zeroReadingCount = 0;

            Log.i(TAG, "Starting Sound Measurement");

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            String outputPath = context.getExternalFilesDir(null) + "/temp_audio_recording.3gp";
            mediaRecorder.setOutputFile(outputPath);

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            new Thread(this::updateDecibels).start();

            return true;

        } catch (IOException | RuntimeException e) {
            Log.e(TAG, "Error initializing MediaRecorder", e);
            listener.onMeasurementError("Failed to start sound measurement: " + e.getMessage());
            resetRecording();
            return false;
        }
    }


    private void updateDecibels() {
        while (isRecording) {
            try {
                Thread.sleep(SAMPLE_INTERVAL);

                // Ensure this runs on the main thread
                if (mediaRecorder != null) {
                    try {
                        double amplitude = mediaRecorder.getMaxAmplitude();

                        Log.d(TAG, "Raw Amplitude: " + amplitude);

                        if (amplitude > 0) {
                            zeroReadingCount = 0;

                            double decibels = 20 * Math.log10(amplitude);

                            Log.i(TAG, String.format("Sound Level: %.1f dB (Amplitude: %.1f)",
                                    decibels, amplitude));

                            notifyDecibelUpdate(decibels);
                        } else {
                            zeroReadingCount++;
                            Log.w(TAG, "Zero amplitude reading. Count: " + zeroReadingCount);

                            if (zeroReadingCount >= MAX_ZERO_READINGS) {
                                Log.e(TAG, "Repeated zero readings. Stopping measurement.");
                                stopSoundMeasurement();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error during amplitude measurement", e);
                        stopSoundMeasurement();
                    }
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "Decibel reading thread interrupted", e);
                break;
            }
        }
    }

    private void notifyDecibelUpdate(double decibels) {
        if (listener != null) {
            listener.onDecibelUpdate(decibels);
        }
    }

    public void stopSoundMeasurement() {
        if (!isRecording) return;

        resetRecording();
    }

    private void resetRecording() {
        isRecording = false;

        if (mediaRecorder != null) {
            try {
                if (isRecording()) {
                    mediaRecorder.stop();
                }
                mediaRecorder.release();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping MediaRecorder", e);
            }
            mediaRecorder = null;
        }

        if (listener != null) {
            listener.onMeasurementStopped();
        }

        Log.i(TAG, "Sound measurement stopped");
    }


    public boolean isRecording() {
        return isRecording;
    }
}