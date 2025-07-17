package com.surajvanshsv.fittrack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 101;

    private TextView stepCountText, distanceText, caloriesText, goalText;
    private ProgressBar progressBar;
    private LineChart lineChart;

    private StepViewModel stepViewModel;
    private String todayDate;

    private static final double STEP_LENGTH_METERS = 0.75;
    private static final double CALORIES_PER_STEP = 0.04;
    private static final int DAILY_GOAL = 8000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize UI
        stepCountText = findViewById(R.id.stepCountText);
        distanceText = findViewById(R.id.distanceText);
        caloriesText = findViewById(R.id.caloriesText);
        goalText = findViewById(R.id.goalText);
        progressBar = findViewById(R.id.goalProgress);
        lineChart = findViewById(R.id.lineChart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
        }

        // Start step tracking service
        Intent serviceIntent = new Intent(this, StepTrackingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Schedule 12AM step reset
        StepResetScheduler.scheduleDailyReset(this);

        // Initialize ViewModel and today's date
        stepViewModel = new ViewModelProvider(this).get(StepViewModel.class);
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Observe today’s steps
        stepViewModel.getStepByDate(todayDate).observe(this, stepEntry -> {
            int steps = (stepEntry != null) ? stepEntry.getSteps() : 0;

            stepCountText.setText(String.valueOf(steps));
            double distanceKm = (steps * STEP_LENGTH_METERS) / 1000.0;
            double calories = steps * CALORIES_PER_STEP;

            distanceText.setText(String.format(Locale.getDefault(), "%.2f km", distanceKm));
            caloriesText.setText(String.format(Locale.getDefault(), "%.1f kcal", calories));

            progressBar.setMax(DAILY_GOAL);
            progressBar.setProgress(steps);

            int percent = (int) ((steps / (float) DAILY_GOAL) * 100);
            goalText.setText(percent + "% of " + DAILY_GOAL);

            // Schedule daily 4PM notification
            StepNotificationScheduler.scheduleDailyNotification(this);
        });

        // Observe and update weekly chart
        stepViewModel.getAllSteps().observe(this, this::updateChart);
    }

    private void updateChart(List<StepEntry> stepEntries) {
        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        long now = System.currentTimeMillis();

        for (StepEntry entry : stepEntries) {
            try {
                Date date = sdf.parse(entry.getDate());
                long daysAgo = (now - date.getTime()) / (1000 * 60 * 60 * 24);
                if (daysAgo <= 6) {
                    entries.add(new Entry(6 - (int) daysAgo, entry.getSteps()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Last 7 Days");
        dataSet.setColor(getColor(R.color.purple_700));
        dataSet.setValueTextColor(getColor(R.color.black));
        dataSet.setCircleRadius(4f);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(getColor(R.color.purple_700));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(100);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setDrawGridLines(false);

        lineChart.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied. Step tracking will not work properly.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
