package com.surajvanshsv.fittrack;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StepRepository {

    private final StepDao stepDao;
    private final LiveData<List<StepEntry>> allStepEntries;
    private final ExecutorService executorService;

    public StepRepository(Application application) {
        StepDatabase db = StepDatabase.getInstance(application);
        stepDao = db.stepDao();
        allStepEntries = stepDao.getAllStepEntries();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(StepEntry stepEntry) {
        executorService.execute(() -> stepDao.insert(stepEntry));
    }

    public void update(StepEntry stepEntry) {
        executorService.execute(() -> stepDao.update(stepEntry));
    }

    public LiveData<List<StepEntry>> getAllStepEntries() {
        return allStepEntries;
    }

    public LiveData<StepEntry> getStepByDate(String date) {
        return stepDao.getStepsByDate(date);
    }

    public LiveData<Integer> getStepCount(String date) {
        return stepDao.getStepCount(date);
    }

    // ✅ Insert or update step count for a given date
    public void insertOrUpdateSteps(String date, int stepCount) {
        executorService.execute(() -> {
            StepEntry existing = stepDao.getStepsByDateRaw(date);
            if (existing != null) {
                existing.setSteps(stepCount);
                stepDao.update(existing);
            } else {
                StepEntry newEntry = new StepEntry(date, stepCount);
                stepDao.insert(newEntry);
            }
        });
    }
}
