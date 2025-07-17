package com.surajvanshsv.fittrack;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface StepDao {

    // Insert or replace step entry for the day
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StepEntry stepEntry);

    // Get only step count as integer for a given date
    @Query("SELECT steps FROM step_table WHERE date = :date LIMIT 1")
    LiveData<Integer> getStepCount(String date);

    // Get full step entry object for UI observation
    @Query("SELECT * FROM step_table WHERE date = :date LIMIT 1")
    LiveData<StepEntry> getStepsByDate(String date);

    // Raw (non-LiveData) fetch, useful for background/thread operations
    @Query("SELECT * FROM step_table WHERE date = :date LIMIT 1")
    StepEntry getStepsByDateRaw(String date);

    // Update step entry
    @Update
    void update(StepEntry stepEntry);

    // Delete step entry
    @Delete
    void delete(StepEntry stepEntry);

    // Get all step entries, most recent first
    @Query("SELECT * FROM step_table ORDER BY date DESC")
    LiveData<List<StepEntry>> getAllStepEntries();
}
