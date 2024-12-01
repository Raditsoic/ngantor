package com.example.ngantor.data.repositories.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ngantor.data.models.SoundLog;

import java.util.List;

@Dao
public interface  SoundLogDao {
    @Insert
    void insert(SoundLog soundLog);

    @Query("SELECT * FROM sound_logs ORDER BY timestamp DESC")
    List<SoundLog> getAllLogs();

    @Query("SELECT * FROM sound_logs WHERE sleepId = :sleepId ORDER BY timestamp ASC")
    List<SoundLog> getLogsBySleepId(int sleepId);
}
