package com.example.ngantor.data.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.ngantor.data.database.AppDatabase;
import com.example.ngantor.data.models.SleepSession;
import com.example.ngantor.data.repositories.dao.SleepSessionDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SleepSessionRepository {
    private final SleepSessionDao sleepSessionDao;
    private final ExecutorService executorService;

    public SleepSessionRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        sleepSessionDao = db.sleepSessionDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insertSleepSession(SleepSession sleepSession) {
        executorService.execute(() -> {
            try {
                sleepSessionDao.insert(sleepSession);
                Log.i("SleepSessionRepository", "Sleep session inserted successfully: " + sleepSession);
            } catch (Exception e) {
                Log.e("SleepSessionRepository", "Error inserting sleep session", e);
            }
        });
    }

    public LiveData<List<SleepSession>> getAllSleepSessions() {
        return sleepSessionDao.getAllSleepSessions();
    }

    public SleepSession getSleepSessionById(int sessionId) {
        return sleepSessionDao.getSessionById(sessionId);
    }

    public int getLatestSleepSessionId() {
        Future<Integer> future = executorService.submit(() -> sleepSessionDao.getLatestSleepSessionId());
        try {
            return future.get();
        } catch (Exception e) {
            Log.e("SleepSessionRepository", "Error getting latest session ID", e);
            return -1;
        }
    }

    public SleepSession getLatestSleepSession() {
        Future<SleepSession> future = executorService.submit(sleepSessionDao::getLatestSleepSession);
        try {
            return future.get();
        } catch (Exception e) {
            Log.e("SleepSessionRepository", "Error getting latest sleep session", e);
            return null;
        }
    }

    public void updateSleepSession(SleepSession session) {
        executorService.execute(() -> sleepSessionDao.update(session));
    }
}