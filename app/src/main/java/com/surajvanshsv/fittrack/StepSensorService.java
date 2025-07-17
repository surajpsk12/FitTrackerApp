package com.surajvanshsv.fittrack;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StepSensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private int initialSteps = -1;
    private int currentSteps = 0;
    private String todayDate;

    private StepDao stepDao;
    private ExecutorService executor;

    private static final String CHANNEL_ID = "step_tracking_channel";
    private static final int NOTIF_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        todayDate = getTodayDate();
        executor = Executors.newSingleThreadExecutor();
        stepDao = StepDatabase.getInstance(this).stepDao();

        createNotificationChannel();
        startForeground(NOTIF_ID, getNotification("Tracking steps..."));
        scheduleMidnightReset();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        executor.shutdown();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 🔄 Sensor Event
    @Override
    public void onSensorChanged(SensorEvent event) {
        int totalStepsSinceReboot = (int) event.values[0];

        if (initialSteps == -1) {
            loadInitialSteps(totalStepsSinceReboot);
        } else {
            currentSteps = totalStepsSinceReboot - initialSteps;
            saveTodaySteps(currentSteps);
        }
    }

    private void loadInitialSteps(int totalStepsSinceBoot) {
        executor.execute(() -> {
            StepEntry existing = stepDao.getStepsByDateRaw(todayDate);
            if (existing != null) {
                currentSteps = existing.getSteps();
                initialSteps = totalStepsSinceBoot - currentSteps;
            } else {
                initialSteps = totalStepsSinceBoot;
                currentSteps = 0;
                stepDao.insert(new StepEntry(todayDate, 0));
            }
        });
    }

    private void saveTodaySteps(int steps) {
        executor.execute(() -> {
            StepEntry todayEntry = stepDao.getStepsByDateRaw(todayDate);
            if (todayEntry == null) {
                todayEntry = new StepEntry(todayDate, steps);
                stepDao.insert(todayEntry);
            } else {
                todayEntry.setSteps(steps);
                stepDao.update(todayEntry);
            }
        });
    }

    // 🔔 Foreground Notification
    private Notification getNotification(String contentText) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FitTrack")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Step Tracking Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    // 📆 Date Formatter
    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed
    }

    // 🕛 Schedule daily reset alarm
    private void scheduleMidnightReset() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        Intent intent = new Intent(this, StepResetReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
