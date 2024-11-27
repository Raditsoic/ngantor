package com.example.ngantor.data.repositories.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ngantor.data.models.SleepSession;

import java.util.List;

@Dao
public interface SleepSessionDao {
    @Insert
    long insert(SleepSession session);

    @Query("SELECT * FROM sleep_session ORDER BY startTime DESC")
    List<SleepSession> getAllSession();
}
