package com.example.ngantor.data.repositories;

import android.content.Context;

import com.example.ngantor.data.database.AppDatabase;
import com.example.ngantor.data.models.SleepSession;
import com.example.ngantor.data.repositories.dao.SleepSessionDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SleepSessionRepository {
    private SleepSessionDao sleepSessionDao;
    private ExecutorService executorService;

    public SleepSessionRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        sleepSessionDao = db.sleepSessionDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public long insertSleepSession(SleepSession session) {
        return sleepSessionDao.insert(session);
    }

    public List<SleepSession> getAllSleepSessions() {
        return sleepSessionDao.getAllSession();
    }
}
