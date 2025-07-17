package com.surajvanshsv.fittrack;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
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
import java.util.Date;
import java.util.Locale;

public class StepTrackingService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int initialSteps = -1;
    private int currentSteps = 0;
    private String currentDate = "";
    private StepRepository repository;

    private static final String CHANNEL_ID = "FitTrackStepChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new StepRepository(getApplication());

        createNotificationChannel();
        startForeground(1, getNotification());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        currentDate = getTodayDate();
        repository.getStepByDate(currentDate).observeForever(entry -> {
            if (entry != null) {
                currentSteps = entry.getSteps();
            }
        });
    }

    private Notification getNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Step Tracking Active")
                .setContentText("FitTrack is counting your steps.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Step Tracking";
            String description = "Background step counter";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Ensures the service is restarted if killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String today = getTodayDate();

        if (!today.equals(currentDate)) {
            // New day — reset step count
            currentDate = today;
            initialSteps = -1;
            currentSteps = 0;
        }

        int totalSteps = (int) event.values[0];

        if (initialSteps == -1) {
            initialSteps = totalSteps;
        }

        int stepsToday = totalSteps - initialSteps;

        if (stepsToday >= 0) {
            currentSteps = stepsToday;
            repository.insertOrUpdateSteps(currentDate, currentSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
