package com.example.ngantor.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sleep_sessions")
public class SleepSession {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private long startTime;
    private long endTime;
    private float lightLevel;

    private float sleepQuality;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }

    public float getLightLevel() { return lightLevel; }
    public void setLightLevel(float lightLevel) { this.lightLevel = lightLevel; }

    public float getSleepQuality() { return sleepQuality; }

    public void setSleepQuality(float sleepQuality) { this.sleepQuality = sleepQuality; }
}
