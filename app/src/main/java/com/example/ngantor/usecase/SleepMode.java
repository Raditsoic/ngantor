package com.example.ngantor.usecase;

import android.content.Context;
import android.util.Log;

import androidx.room.util.FileUtil;

import com.example.ngantor.data.models.SleepSession;
import com.example.ngantor.data.models.SoundLog;
import com.example.ngantor.data.repositories.SleepSessionRepository;
import com.example.ngantor.data.repositories.SoundLogRepository;
import com.example.ngantor.utils.ModelLoader;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SleepMode {
    private final SleepSessionRepository sleepSessionRepository;
    private final SoundLogRepository soundLogRepository;
    private final LuxSensor luxSensor;
    private final DecibelSensor decibelSensor;
    private final Context context;
    private final Interpreter sleepQualityModel;

    public boolean active;
    private Timer soundRecordingTimer;
    private final List<Double> soundLevels;

    private ScheduledExecutorService executorService;
    private final ScheduledExecutorService databaseExecutor;

    public SleepMode(Context context) {
        this.context = context.getApplicationContext();
        soundLogRepository = new SoundLogRepository(context);
        sleepSessionRepository = new SleepSessionRepository(context);
        this.luxSensor = new LuxSensor(context);
        this.decibelSensor = new DecibelSensor(context);
        this.soundLevels = new ArrayList<>();
        this.databaseExecutor = Executors.newSingleThreadScheduledExecutor();
        active = false;

        this.sleepQualityModel = ModelLoader.loadModel(context, "sleep_quality.tflite");
    }

    public void StartSleep() {
        active = true;

        if (!decibelSensor.start()) {
            Log.e("SleepMode", "Failed to start DecibelSensor. Aborting StartSleep.");
            active = false;
            return;
        }

        databaseExecutor.execute(() -> {
            int sleepSessionId = createSleepSession();
            context.getMainExecutor().execute(() -> {
                if (sleepSessionId != -1) {
                    startSoundRecordingTimer(sleepSessionId);
                }
            });
        });
    }

    private int createSleepSession() {
        float luxLevel = this.luxSensor.getLuxLevel();
        long startTime = System.currentTimeMillis();

        SleepSession sleepSession = new SleepSession();
        sleepSession.setStartTime(startTime);
        sleepSession.setLightLevel(luxLevel);
        sleepSession.setEndTime(-1);

        try {
            sleepSessionRepository.insertSleepSession(sleepSession);
            return sleepSessionRepository.getLatestSleepSessionId();
        } catch (Exception e) {
            Log.e("SleepMode", "Error creating sleep session", e);
            return -1;
        }
    }

    private void startSoundRecordingTimer(int sleepSessionId) {
        executorService = Executors.newSingleThreadScheduledExecutor();
        soundLevels.clear();
        executorService.scheduleAtFixedRate(() -> {
            if (!active) {
                executorService.shutdown();
                return;
            }
            double currentSoundLevel = decibelSensor.getDecibelLevel();
            soundLevels.add(currentSoundLevel);

            if (soundLevels.size() >= 10) {
                int averageDecibel = calculateAverageSoundLevel();
                saveSoundLog(sleepSessionId, averageDecibel);
                soundLevels.clear();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private int calculateAverageSoundLevel() {
        if (soundLevels.isEmpty()) {
            return 0;
        }

        int sum = 0;
        for (Double level : soundLevels) {
            sum += level;
        }
        return sum / soundLevels.size();
    }

    private void saveSoundLog(int sleepSessionId, int averageDecibel) {
        try {
            SoundLog soundLog = new SoundLog();
            soundLog.setSleepId(sleepSessionId);
            soundLog.setDecibel(averageDecibel);
            soundLog.setTimestamp(System.currentTimeMillis());

            soundLogRepository.insertLog(soundLog);
        } catch (Exception e) {
            Log.e("SleepMode", "Error saving sound log", e);
        }
    }

    public void EndSleep() {
        active = false;

        decibelSensor.stop();

        if (soundRecordingTimer != null) {
            soundRecordingTimer.cancel();
        }

        long endTime = System.currentTimeMillis();

        databaseExecutor.execute(() -> {
            try {
                SleepSession latestSession = sleepSessionRepository.getLatestSleepSession();
                if (latestSession != null) {
                    // Calculate sleep duration
                    float sleepDuration = calculateSleepDuration(latestSession.getStartTime(), endTime);

                    // Get light level
                    float lightLevel = latestSession.getLightLevel();

                    // Predict sleep quality
                    float sleepQuality = predictSleepQuality(sleepDuration, lightLevel);
                    Log.i("Sleep Quality", String.valueOf(sleepQuality));
                    // Update sleep session
                    latestSession.setEndTime(endTime);
                    latestSession.setSleepQuality(sleepQuality);
                    sleepSessionRepository.updateSleepSession(latestSession);
                }
            } catch (Exception e) {
                Log.e("SleepMode", "Error ending sleep session", e);
            }
        });
    }

    private float calculateSleepDuration(long startTime, long endTime) {
        // Convert milliseconds to hours
        return (endTime - startTime) / (1000f * 60 * 60);
    }

    private float predictSleepQuality(float sleepDuration, float lightLevel) {
        if (sleepQualityModel == null) {
            Log.e("SleepMode", "Sleep quality model is not loaded");
            return -1f;
        }

        // Prepare input
        float[][] input = {{sleepDuration, lightLevel}};
        float[][] output = new float[1][1];

        // Run inference
        sleepQualityModel.run(input, output);

        return output[0][0];
    }

    public void cleanup() {
        if (sleepQualityModel != null) {
            sleepQualityModel.close();
        }
        databaseExecutor.shutdown();
    }

}