package com.surajvanshsv.fittrack;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class StepViewModel extends AndroidViewModel {

    private final StepRepository repository;
    private final LiveData<List<StepEntry>> allSteps;

    public StepViewModel(@NonNull Application application) {
        super(application);
        repository = new StepRepository(application);
        allSteps = repository.getAllStepEntries();
    }

    public void insert(StepEntry stepEntry) {
        repository.insert(stepEntry);
    }

    public void update(StepEntry stepEntry) {
        repository.update(stepEntry);
    }

    // ✅ Removed "delete" because StepRepository doesn't have it
    // public void delete(StepEntry stepEntry) {
    //     repository.delete(stepEntry);
    // }

    public LiveData<List<StepEntry>> getAllSteps() {
        return allSteps;
    }

    public LiveData<StepEntry> getStepByDate(String date) {
        return repository.getStepByDate(date);
    }

    public LiveData<Integer> getStepCount(String date) {
        return repository.getStepCount(date);
    }

    public void insertOrUpdateSteps(String date, int stepCount) {
        repository.insertOrUpdateSteps(date, stepCount);
    }
}
