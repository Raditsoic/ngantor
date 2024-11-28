package com.example.ngantor.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sleep_session")
public class SleepSession {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private long startTime;
    private long endTime;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
}
