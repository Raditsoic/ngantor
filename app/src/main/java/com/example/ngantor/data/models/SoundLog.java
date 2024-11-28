package com.example.ngantor.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sound_log")
public class SoundLog {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private long timestamp;
    private float decibel;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    private int sleepId;

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public float getDecibel() { return decibel; }
    public void setDecibel(float decibel) { this.decibel = decibel; }

    public int getSleepId() { return sleepId; }
    public void setSleepId(int sleepId) { this.sleepId = sleepId; }
}
