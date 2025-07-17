package com.surajvanshsv.fittrack;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class StepNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int steps = intent.getIntExtra("steps", 0);
        String date = intent.getStringExtra("date");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel for Android 8+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("steps_channel", "Daily Step Reminder", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Shows daily steps at 4 PM");
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "steps_channel")
                .setSmallIcon(R.drawable.ic_baseline_directions_walk_24)
                .setContentTitle("Your Steps Today")
                .setContentText("You’ve walked " + steps + " steps today (" + date + ") 👣")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify(200, builder.build());
    }
}
