package com.example.ngantor.data.repositories.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ngantor.data.models.SleepSession;

import java.util.List;

@Dao
public interface SleepSessionDao {
    @Insert
    void insert(SleepSession session);

    @Query("SELECT * FROM sleep_sessions ORDER BY startTime DESC")
    List<SleepSession> getAllSession();

    @Query("SELECT * FROM sleep_sessions WHERE id = :sessionId")
    SleepSession getSessionById(int sessionId);

    @Query("SELECT id FROM sleep_sessions ORDER BY startTime DESC LIMIT 1")
    Integer getLatestSleepSessionId();

    @Query("SELECT * FROM sleep_sessions ORDER BY startTime DESC LIMIT 1")
    SleepSession getLatestSleepSession();

    @Update
    void update(SleepSession session);
}
