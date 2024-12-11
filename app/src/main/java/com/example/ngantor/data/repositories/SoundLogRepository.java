package com.example.ngantor.data.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.ngantor.data.database.AppDatabase;
import com.example.ngantor.data.models.SoundLog;
import com.example.ngantor.data.repositories.dao.SoundLogDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundLogRepository {
    private SoundLogDao soundLogDao;
    private ExecutorService executorService;

    public SoundLogRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        soundLogDao = db.soundLogDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insertLog(SoundLog soundLog) {
        executorService.execute(() -> soundLogDao.insert(soundLog));
    }

    public List<SoundLog> getAllLogs() {
        return soundLogDao.getAllLogs();
    }

    public LiveData<List<SoundLog>> getLogsBySleepId(int sleepId) {
        return soundLogDao.getLogsBySleepId(sleepId);
    }
}
