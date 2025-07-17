package com.surajvanshsv.fittrack;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "step_table")
public class StepEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String date; // Format: "YYYY-MM-DD"

    private int steps;

    public StepEntry(@NonNull String date, int steps) {
        this.date = date;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
