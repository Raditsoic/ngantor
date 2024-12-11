package com.example.ngantor.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ngantor.data.models.SleepSession;
import com.example.ngantor.data.models.SoundLog;
import com.example.ngantor.data.repositories.dao.SleepSessionDao;
import com.example.ngantor.data.repositories.dao.SoundLogDao;


@Database(entities = {SoundLog.class, SleepSession.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract SoundLogDao soundLogDao();
    public abstract SleepSessionDao sleepSessionDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "ngantor_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return  INSTANCE;
    }
}
