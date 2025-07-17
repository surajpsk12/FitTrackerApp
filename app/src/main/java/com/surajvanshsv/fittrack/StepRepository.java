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
    public LiveData<StepEntry> getStep(String date) {
        return stepDao.getStepsByDate(date);
    }

    // 🔹 Basic Insert
    public void insert(StepEntry stepEntry) {
        executorService.execute(() -> stepDao.insert(stepEntry));
    }

    // 🔹 Basic Update
    public void update(StepEntry stepEntry) {
        executorService.execute(() -> stepDao.update(stepEntry));
    }

    // 🔹 All steps LiveData (used for listing in UI)
    public LiveData<List<StepEntry>> getAllStepEntries() {
        return allStepEntries;
    }

    // 🔹 Get LiveData StepEntry by date
    public LiveData<StepEntry> getStepByDate(String date) {
        return stepDao.getStepsByDate(date);
    }

    // 🔹 Get LiveData step count by date
    public LiveData<Integer> getStepCount(String date) {
        return stepDao.getStepCount(date);
    }

    // 🔹 Insert or update step entry for a specific day
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

    // ✅ Used in chart to fetch last 7 days
    public void getLast7DaysSteps(ChartCallback callback) {
        executorService.execute(() -> {
            List<StepEntry> recentSteps = stepDao.getLast7DaysStepsRaw();
            callback.onDataFetched(recentSteps);
        });
    }

    // ✅ Optional: fetch single day's entry in background (used in midnight reset)
    public void getStepEntryRaw(String date, StepEntryCallback callback) {
        executorService.execute(() -> {
            StepEntry entry = stepDao.getStepsByDateRaw(date);
            callback.onStepFetched(entry);
        });
    }

    // 🔹 Chart callback
    public interface ChartCallback {
        void onDataFetched(List<StepEntry> last7Days);
    }

    // 🔹 Single entry callback (for reset or restoration logic)
    public interface StepEntryCallback {
        void onStepFetched(StepEntry entry);
    }
}
