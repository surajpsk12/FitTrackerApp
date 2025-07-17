package com.surajvanshsv.fittrack;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {StepEntry.class}, version = 1, exportSchema = false)
public abstract class StepDatabase extends RoomDatabase {

    private static volatile StepDatabase instance;

    public abstract StepDao stepDao();

    public static StepDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (StepDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    StepDatabase.class,
                                    "step_database"
                            ).fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
