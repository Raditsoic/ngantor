package com.example.ngantor.data.repositories;

import android.content.Context;
import android.util.Log;

import com.example.ngantor.data.database.AppDatabase;
import com.example.ngantor.data.models.SleepSession;
import com.example.ngantor.data.repositories.dao.SleepSessionDao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SleepSessionRepository {
    private SleepSessionDao sleepSessionDao;
    private ExecutorService executorService;

    public SleepSessionRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        sleepSessionDao = db.sleepSessionDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insertSleepSession(SleepSession sleepSession) {
        try {
            sleepSessionDao.insert(sleepSession);
            Log.i("SleepSessionRepository", "Sleep session inserted successfully: " + sleepSession);
        } catch (Exception e) {
            Log.e("SleepSessionRepository", "Error inserting sleep session", e);
        }
    }

    public int getLatestSleepSessionId() {
        return sleepSessionDao.getLatestSleepSessionId();
    }

    public SleepSession getLatestSleepSession() {
        return sleepSessionDao.getLatestSleepSession();
    }

    public List<SleepSession> getAllSleepSessions() {
        return sleepSessionDao.getAllSession();
    }

    public SleepSession getSleepSessionById(int sessionId) {
        return sleepSessionDao.getSessionById(sessionId);
    }

    public void updateSleepSession(SleepSession session) {
        executorService.execute(() -> sleepSessionDao.update(session));
    }
}
