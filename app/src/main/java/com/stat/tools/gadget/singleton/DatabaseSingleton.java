package com.stat.tools.gadget.singleton;

import android.content.Context;

import androidx.room.Room;

import com.stat.tools.gadget.data.AppDatabase;

/** Singleton class for managing the single instance of the AppDatabase
 */
public class DatabaseSingleton {

    private static volatile AppDatabase instance;
    private static final Object LOCK = new Object();

    // Private constructor to prevent instantiation
    private DatabaseSingleton() {}


    /**
     * Gets the singleton instance of the AppDatabase.
     * If the instance is not created yet, it initializes the database.
     * Uses double-checked locking to ensure thread safety and performance.
     *
     * @param context The application context used to build the database.
     * @return The singleton instance of the AppDatabase.
     */
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "Player-name"
                    ).allowMainThreadQueries().build();
                }
            }
        }
        return instance;
    }
}
