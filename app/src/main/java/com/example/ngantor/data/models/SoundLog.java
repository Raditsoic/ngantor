package com.example.ngantor.data.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "sound_logs",
        foreignKeys = @ForeignKey(
                entity = SleepSession.class,
                parentColumns = "id",
                childColumns = "sleepId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "sleepId")}
)
public class SoundLog {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private long timestamp;
    private float decibel;
    private int sleepId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public float getDecibel() { return decibel; }
    public void setDecibel(float decibel) { this.decibel = decibel; }

    public int getSleepId() { return sleepId; }
    public void setSleepId(int sleepId) { this.sleepId = sleepId; }
}
