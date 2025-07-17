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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StepEntry stepEntry);

    @Update
    void update(StepEntry stepEntry);

    @Delete
    void delete(StepEntry stepEntry);

    // 🔹 Get today's step count as LiveData
    @Query("SELECT steps FROM step_table WHERE date = :date LIMIT 1")
    LiveData<Integer> getStepCount(String date);

    // 🔹 Get today's StepEntry as LiveData
    @Query("SELECT * FROM step_table WHERE date = :date LIMIT 1")
    LiveData<StepEntry> getStepsByDate(String date);

    // 🔹 Get today's StepEntry as plain object (non-LiveData) — for background logic
    @Query("SELECT * FROM step_table WHERE date = :date LIMIT 1")
    StepEntry getStepsByDateRaw(String date);

    // 🔹 Get all entries, sorted latest-first
    @Query("SELECT * FROM step_table ORDER BY date DESC")
    LiveData<List<StepEntry>> getAllStepEntries();

    // ✅ For chart: Get last 7 days in ascending order
    @Query("SELECT * FROM step_table ORDER BY date DESC LIMIT 7")
    List<StepEntry> getLast7DaysStepsRaw();

    // ✅ Optional: Get only steps for chart (LiveData)
    @Query("SELECT steps FROM step_table ORDER BY date DESC LIMIT 7")
    LiveData<List<Integer>> getLast7DaysStepCounts();
}
