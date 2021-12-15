package ru.hse.control_system_v2;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.google.android.material.color.DynamicColors;

public class App extends Application {

    public static App instance;

    private static AppDataBase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDataBase.class, "devices")
                .allowMainThreadQueries()
                .build();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }

    public static App getInstance() {
        return instance;
    }

    public static AppDataBase getDatabase() {
        return database;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

}