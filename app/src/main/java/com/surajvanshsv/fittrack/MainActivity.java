package com.surajvanshsv.fittrack;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 101;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private SensorEventListener stepListener;

    private int initialStepCount = -1;
    private TextView stepCountText, distanceText, caloriesText;

    private StepViewModel stepViewModel;
    private String todayDate;

    private static final double STEP_LENGTH_METERS = 0.75;
    private static final double CALORIES_PER_STEP = 0.04;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        stepCountText = findViewById(R.id.stepCountText);
        distanceText = findViewById(R.id.distanceText);
        caloriesText = findViewById(R.id.caloriesText);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Request permission if required (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
            }
        }

        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        stepViewModel = new ViewModelProvider(this).get(StepViewModel.class);

        // Observe step data from database
        stepViewModel.getStepByDate(todayDate).observe(this, stepEntry -> {
            if (stepEntry != null) {
                int steps = stepEntry.getSteps();
                stepCountText.setText(String.valueOf(steps));

                double distanceInKm = (steps * STEP_LENGTH_METERS) / 1000.0;
                double calories = steps * CALORIES_PER_STEP;

                distanceText.setText(String.format(Locale.getDefault(), "%.2f km", distanceInKm));
                caloriesText.setText(String.format(Locale.getDefault(), "%.1f kcal", calories));
            } else {
                stepCountText.setText("0");
                distanceText.setText("0.00 km");
                caloriesText.setText("0.0 kcal");
            }
        });

        // Setup sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        stepListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                int totalSteps = (int) event.values[0];

                if (initialStepCount == -1) {
                    initialStepCount = totalSteps;
                }

                int currentSteps = totalSteps - initialStepCount;

                // Save or update today's step count
                stepViewModel.insertOrUpdateSteps(todayDate, currentSteps);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Not used
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepCounterSensor != null) {
            sensorManager.registerListener(stepListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Step Counter not available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(stepListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied. Step counter will not work.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
